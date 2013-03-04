package se.kth.ict.id2203.riwcm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import se.kth.ict.id2203.riwcport.*;
import se.kth.ict.id2203.babport.*;
import se.kth.ict.id2203.pp2p.*;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.launch.Topology;

public class RIWCM extends ComponentDefinition {
	private class ReadValues {
		long t;
		int r;
		int v;

		public ReadValues(long t, int r, int v) {
			this.t = t;
			this.r = r;
			this.v = v;
		}
	}

	Negative<ReadImposeWriteConsult> riwcm = provides(ReadImposeWriteConsult.class);
	Positive<BasicBroadcast> bab = requires(BasicBroadcast.class);
	Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);

	private Address self;
	private Topology topology;
	private int regNum;
	private HashMap<Integer, ArrayList<Address>> writeSet;
	private HashMap<Integer, ArrayList<ReadValues>> readSet;
	private boolean reading[];
	private int reqId[];
	private int readval[];
	private int writeval[];
	private int v[];
	private long ts[];
	private int mrank[];

	private int i;

	public RIWCM() {
		subscribe(handleInit, control);
		subscribe(handleReadRequest, riwcm);
		subscribe(handleWriteRequest, riwcm);
		subscribe(handleBabWriteMessage, bab);
		subscribe(handleBabReadMessage, bab);
		subscribe(handleAckMessage, pp2p);
		subscribe(handleReadValMessage, pp2p);
	}

	Handler<RIWCMInit> handleInit = new Handler<RIWCMInit>() {
		public void handle(RIWCMInit event) {
			topology = event.getTopology();
			self = topology.getSelfAddress();
			readSet = new HashMap<Integer, ArrayList<ReadValues>>();
			writeSet = new HashMap<Integer, ArrayList<Address>>();
			regNum = event.getRegNum();
			reading = new boolean[regNum];
			reqId = new int[regNum];
			readval = new int[regNum];
			writeval = new int[regNum];
			v = new int[regNum];
			ts = new long[regNum];
			mrank = new int[regNum];
			i = self.getId() -1;
			for (int j = 0; j < regNum; j++) {
				writeSet.put(j, new ArrayList<Address>());
				readSet.put(j, new ArrayList<ReadValues>());
				reading[j] = false;
				reqId[j] = 0;
				readval[j] = 0;
				v[j] = 0;
				ts[j] = 0;
				mrank[j] = 0;
			}
		}
	};

	Handler<ReadMessage> handleReadRequest = new Handler<ReadMessage>() {
		public void handle(ReadMessage event) {
			// System.out.println("Received a read request");
			Address s = event.getSource();
			int reg = event.getRegister();

			/* Update reqId */
			reqId[reg] = reqId[reg] + 1;

			/* Update reading */
			reading[reg] = true;

			/* Update write Set */
			writeSet.remove(reg);
			writeSet.put(reg, new ArrayList<Address>());

			/* Update read Set */
			readSet.remove(reg);
			readSet.put(reg, new ArrayList<ReadValues>());

			/* trigger bebBroadcast event */
			//System.out.println("Received a read request and triggers to beb read with id "+reqId[reg]);
			trigger(new BabReadMessage(self, reg, reqId[reg]), bab);

		}
	};

	Handler<WriteMessage> handleWriteRequest = new Handler<WriteMessage>() {
		public void handle(WriteMessage event) {

			Address s = event.getSource();
			int reg = event.getRegister();
			int val = event.getValue();
			/* Update reqId */
			reqId[reg] = reqId[reg] + 1;
			/* Update write Set */
			writeval[reg] = val;
			writeSet.remove(reg);
			writeSet.put(reg, new ArrayList<Address>());

			/* Update read Set */
			readSet.remove(reg);
			readSet.put(reg, new ArrayList<ReadValues>());

			/* trigger bebBroadcast event */
			//System.out.println("Received a write request and triggers to beb read with id "+reqId[reg]);
			trigger(new BabReadMessage(self, reg, reqId[reg]), bab);
		}
	};

	Handler<BabReadMessage> handleBabReadMessage = new Handler<BabReadMessage>() {
		public void handle(BabReadMessage event) {
			int reg = event.getRegister();
			int id = event.getId();
			//System.out.println("Received a bab read request and triggers to psp read with value "+v[reg]);
			trigger(new Pp2pSend(event.getSource(), new ReadValMessage(self,
					reg, id, ts[reg], mrank[reg], v[reg])), pp2p);
		}
	};

	Handler<BabWriteMessage> handleBabWriteMessage = new Handler<BabWriteMessage>() {
		public void handle(BabWriteMessage event) {
			int r = event.getRegister();
			int id = event.getId();
			long t = event.getTimestamp();
			int j = event.getRank();
			int val = event.getValue();
			if (t > ts[r])  {
				v[r] = val;
				ts[r] = t;
				mrank[r] = j;
			}
			else if (t == ts[r] && j > mrank[r] ) {
				v[r] = val;
				ts[r] = t;
				mrank[r] = j;
			}
			//System.out.println("Received a bab write request and triggers to p2p ack with id "+id+"at source "+event.getSource());
			trigger(new Pp2pSend(event.getSource(), new AckMessage(self, r, id)),
					pp2p);
		}
	};

	Handler<ReadValMessage> handleReadValMessage = new Handler<ReadValMessage>() {
		public void handle(ReadValMessage event) {

			Address s = event.getSource();
			int r = event.getReg();
			int id = event.getId();
			long t = event.getTimestamp();
			int rk = event.getRank();
			int val = event.getValue();
			//System.out.println("Received a readval request and checks read req with id "+id+" and reqID "+reqId[r]);
			if (id == reqId[r]) {
				ArrayList<ReadValues> al = readSet.get(r);
					al.add(new ReadValues(t, rk, val));
					readSet.remove(r);
					readSet.put(r, al);
				if (al.size() > (topology.getNeighbors(self).size()+1) / 2.0 ) {
					ReadExist(s, r);
				}
			}
		}
	};

	Handler<AckMessage> handleAckMessage = new Handler<AckMessage>() {
		public void handle(AckMessage event) {

			Address s = event.getSource();
				int r = event.getReg();
				int id = event.getId();
				//System.out.println("Received an ack request and checks write req with id "+id+" and reqID "+reqId[r]+"from "+event.getSource());
				// System.out.println("Received an answer from p2p with id "+id+" and reqId "+reqId[r]);
				if (id == reqId[r]) {
					ArrayList<Address> al = writeSet.get(r);
					if (!al.contains(s))
						al.add(s);
					writeSet.remove(r);
					writeSet.put(r, al);
					if (al.size() > (topology.getNeighbors(self).size()+1) / 2.0 )  {
						WriteExist(s, r);
					}
				}
		}
	};

	void WriteExist(Address source, int r) {
		//System.out.println("write...");
		if (reading[r] == true) {
			reading[r] = false;
			// System.out.println("returning read value to "+source);
			trigger(new ReadMessageReturn(source, r, readval[r]), riwcm);
		} else{
			trigger(new WriteMessageReturn(source, r), riwcm);
		}
	}
	
	void ReadExist(Address source, int r) {
		//System.out.println("read ..");
		/*Find the highest*/
		ArrayList<ReadValues> al = readSet.get(r);
		int highest = 0;
		for (int j=0; j<al.size() ; j++) {
			if (al.get(j).t > al.get(highest).t)
				highest = j;
			else if (al.get(j).t == al.get(highest).t  && al.get(j).r > al.get(highest).r)
				highest = j;
		}
		readval[r] = al.get(highest).v;
		if (reading[r] == true)
			trigger(new BabWriteMessage(source, r, reqId[r], al.get(highest).t , al.get(highest).r, readval[r]), bab);
		else
			trigger(new BabWriteMessage(source, r, reqId[r],al.get(highest).t+1 , i, writeval[r]), bab);
	}
}