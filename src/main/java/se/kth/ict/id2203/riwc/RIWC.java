package se.kth.ict.id2203.riwc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import se.kth.ict.id2203.riwcport.*;
import se.kth.ict.id2203.subport.UnDeliver;
import se.kth.ict.id2203.pfdport.*;
import se.kth.ict.id2203.bebport.*;
import se.kth.ict.id2203.pp2p.*;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.launch.Topology;

public class RIWC extends ComponentDefinition {
	Negative<ReadImposeWriteConsult> riwc = provides(ReadImposeWriteConsult.class);
	Positive<PerfectFailureDetector> pfd = requires(PerfectFailureDetector.class);
	Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
	Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
	
	private Address self;
	private Topology topology; 
	private int regNum;
	private Set<Address> correct;
	private HashMap<Integer, ArrayList<Address>> writeSet; 
	private boolean reading[];
	private int reqId[];
	private int readval[];
	private int v[];
	private long ts[];
	private int mrank[];
	
	private int i;
	
	
	public RIWC() {
		subscribe(handleInit, control);
		subscribe(handleCrash, pfd);
		subscribe(handleReadRequest, riwc);
		subscribe(handleWriteRequest, riwc);
		subscribe(handleBebMessage, beb);
		subscribe(handleAckMessage, pp2p);
	}
	
	Handler<RIWCInit> handleInit = new Handler<RIWCInit>() {
		public void handle(RIWCInit event) {
			topology = event.getTopology();
			self = topology.getSelfAddress();
			correct = new HashSet<Address>();
			writeSet = new HashMap<Integer, ArrayList<Address>>();
			regNum = event.getRegNum();
			reading = new boolean[regNum];
			reqId = new int[regNum];
			readval = new int[regNum];
			v = new int[regNum];
			ts = new long[regNum];
			mrank = new int[regNum];
			i = self.getId() - 1;
			for (Address neighbor : topology.getNeighbors(self)) {
				correct.add(neighbor);
			}
			for (int j=0 ; j<regNum ; j++) {
				writeSet.put(j, new ArrayList<Address>());
				reading[j]=false;
				reqId[j] = 0;
				readval[j]=0;
				v[j]= 0;
				ts[j] = 0;
				mrank[j] = 0;
			}
		}
	};
	
	Handler<PfdCrash> handleCrash = new Handler<PfdCrash>() {
		public void handle(PfdCrash event) {
			System.out.println("Crash from "+event.getSource());
			correct.remove(event.getSource());
		}
	};
	
	Handler<ReadMessage> handleReadRequest = new Handler<ReadMessage>() {
		public void handle(ReadMessage event) {
			//System.out.println("Received a read request");
			Address self = event.getSource();
			int reg = event.getRegister();
			
			/*Update reqId*/
			reqId[reg] = reqId[reg]+1;
			
			/*Update reading*/
			reading[reg] = true;
			
			/*Update write Set*/
			writeSet.remove(reg);
			writeSet.put(reg, new ArrayList<Address>());
			
			/*Update readval*/
			readval[reg] = v[reg];
			
			/*trigger bebBroadcast event*/
			trigger(new BebMessage(self,reg, reqId[reg], ts[reg], mrank[reg], v[reg]), beb);
			
		}
	};
	
	Handler<WriteMessage> handleWriteRequest = new Handler<WriteMessage>() {
		public void handle(WriteMessage event) {
			
			Address s = event.getSource();
			int reg = event.getRegister();
			/*Update reqId*/
			reqId[reg] = reqId[reg]+1;
			/*Update write Set*/
			writeSet.remove(reg);
			writeSet.put(reg, new ArrayList<Address>());
			/*trigger bebBroadcast event*/
			//System.out.println("Received a write request and triggers to beb the value"+event.getValue());
			trigger(new BebMessage(self,reg, reqId[reg], ts[reg]+1, i, event.getValue()), beb);
		}
	};
	
	
	Handler<BebMessage> handleBebMessage = new Handler<BebMessage>() {
		public void handle(BebMessage event) {
			
			int r = event.getRegister();
			int id = event.getId();
			long t = event.getTimestamp();
			int j = event.getRank();
			int val = event.getValue();
			
			if (t>ts[r] && j>mrank[r]) {
				v[r]=val;
				ts[r]=t;
				mrank[r]=j;
			}
			//System.out.println("Received an answer from beb and triggers the reg to p2p");
			trigger(new Pp2pSend(event.getSource(), new AckMessage(self, r, id)), pp2p);
		}
	};
	
	Handler<AckMessage> handleAckMessage = new Handler<AckMessage>() {
		public void handle(AckMessage event) {
			
			Address s = event.getSource();
			int r = event.getReg();
			int id = event.getId();
			//System.out.println("Received an answer from p2p with id "+id+" and reqId "+reqId[r]);
			if (id==reqId[r]) {
				ArrayList<Address> al = writeSet.get(r);
				al.add(s);
				writeSet.remove(r);
				writeSet.put(r, al);
				
				/*if correct subset of al*/
				boolean exist = true;
				for (Address c : correct) {
					if (!al.contains(c)) {
						exist = false;
						break;
					}
				}
				if (exist == true)
					existCorrect(s, r);
			}
		}
	};
	
	void existCorrect(Address source, int r) {
		
		if (reading[r] == true) {
			reading[r] = false;
			//System.out.println("returning read value to "+source);
			trigger(new ReadMessageReturn(source, r, readval[r]), riwc);
		}
		else {
			trigger(new WriteMessageReturn(source, r), riwc);
		}
	}
}