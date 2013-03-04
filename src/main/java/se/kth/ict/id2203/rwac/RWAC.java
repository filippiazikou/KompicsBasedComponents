package se.kth.ict.id2203.rwac;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import se.kth.ict.id2203.rwacport.*;
import se.kth.ict.id2203.babcport.*;
import se.kth.ict.id2203.pp2p.*;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.launch.NoLinkException;
import se.sics.kompics.launch.Topology;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

public class RWAC extends ComponentDefinition{
	private class Read {
		int value;
		long ts;
		public Read(int value, long ts) {
			this.value = value;
			this.ts = ts;
		}
		
	}
	Negative<AbortableConsensus> ac = provides(AbortableConsensus.class);

	Positive<BasicBroadcast> bab = requires(BasicBroadcast.class);
	Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);

	private Address self;
	private Topology topology;
	private Set<Integer> seenIds;
	private HashMap<Integer, Set<Read>> readSet;
	private HashMap<Integer, Integer> tempValue;
	private HashMap<Integer, Integer> val;
	private HashMap<Integer, Integer> wAcks;
	private HashMap<Integer, Long> rts;
	private HashMap<Integer, Long> wts;
	private HashMap<Integer, Integer> tstamp;
	
	private int majority;

	private void initInstance(int id) {
		if (!seenIds.contains(id)) {
			tempValue.put(id, -1);
			val.put(id, -1);
			wAcks.put(id, 0);
			rts.put(id, (long) 0);
			wts.put(id, (long) 0);
			tstamp.put(id, self.getId());
			readSet.put(id, new HashSet<RWAC.Read>());
			seenIds.add(id);
		}
	}
	
	public RWAC() {
		subscribe(handleInit, control);
		subscribe(handlePropose, ac);
		subscribe(handleReadDeliver, bab);
		subscribe(handleNackDeliver, pp2p);
		subscribe(handleReadAckDeliver, pp2p);
		subscribe(handleWriteDeliver, bab);
		subscribe(handleWriteAckDeliver, pp2p);
	}
	
	Handler<RWACInit> handleInit = new Handler<RWACInit>() {
		public void handle(RWACInit event) {
			topology = event.getTopology();
			self = topology.getSelfAddress();
			seenIds = new HashSet<Integer>();
			majority =  (int) (Math.floor(topology.getAllAddresses().size()/2.0)+1);
			
			readSet = new HashMap<Integer, Set<Read>>();
			tempValue = new HashMap<Integer, Integer>();
			val = new HashMap<Integer, Integer>() ;
			wAcks = new HashMap<Integer, Integer>();
			rts = new HashMap<Integer, Long>();
			wts = new HashMap<Integer, Long>() ;
			tstamp = new HashMap<Integer, Integer>() ;
			
		}
	};
	
	Handler<AcPropose> handlePropose = new Handler<AcPropose>() {
		public void handle(AcPropose event) {
			int id = event.getId();
			int v = event.getValue();
			initInstance(id);
			
			
			//System.out.println("printing the value.." + v);
			
			/*increase tstamp*/
			int tstampv = tstamp.get(id) + topology.getAllAddresses().size();
			tstamp.put(id, tstampv);
			
			/*initiate tempvalue*/
			tempValue.put(id, v);
			
			/*broadcast the value*/
			trigger(new BabReadMessage(self, tstampv, id), bab);
			
		}
	};
	
	Handler<BabReadMessage> handleReadDeliver = new Handler<BabReadMessage>() {
		public void handle(BabReadMessage event) {
			Address source = event.getSource();
			int id = event.getId();
			long ts = event.getTimestamp();
			initInstance(id);
			if (rts.get(id) >= ts || wts.get(id)>=ts) {
				trigger(new Pp2pSend(source, new NackMessage(self, id)), pp2p);
			}
			else {
				rts.put(id, ts);
				trigger(new Pp2pSend(source, new ReadAck(self, id, wts.get(id), val.get(id), ts)), pp2p);
			}
		}
	};
	
	Handler<NackMessage> handleNackDeliver = new Handler<NackMessage>() {
		public void handle(NackMessage event) {
			//System.out.println("received an nack message... ");
			Address source = event.getSource();
			int id = event.getId();
			readSet.put(id, new HashSet<RWAC.Read>());
			wAcks.put(id, 0);
			trigger(new AcDecide(id, -1), ac);
		}
	};
	
	Handler<ReadAck> handleReadAckDeliver = new Handler<ReadAck>() {
		public void handle(ReadAck event) {
			int id = event.getId();
			long ts = event.getWts();
			int v = event.getVal();
			long sentts = event.getTs();
			
			if (sentts == tstamp.get(id)) {
				Set<Read> tmp = readSet.get(id);
				tmp.add(new Read(v, ts));
				readSet.put(id, tmp);
				
				tmp = readSet.get(id);
				if (tmp.size() == majority) {
					/*Get the highest timestamp*/
					long highest = -1;
					int highestV = -1;
					//System.out.println("Finding the max: ");
					for (Read r : tmp) {
						//System.out.println(r.ts+" "+r.value);
						if (r.ts > highest) {
							highest = r.ts;
							highestV = r.value;
						}
						else if (r.ts == highest && r.value > highestV ) {
							highest = r.ts;
							highestV = r.value;
						}
					}
					//System.out.println("highest: "+highestV);
					if (highestV != -1) 
						tempValue.put(id, highestV);
					trigger(new BabWriteMessage(self, id, tstamp.get(id), tempValue.get(id)), bab);
				}
			}
		}
	};

	Handler<BabWriteMessage> handleWriteDeliver = new Handler<BabWriteMessage>() {
		public void handle(BabWriteMessage event) {
			Address source = event.getSource();
			int id = event.getId();
			long ts = event.getTimestamp();
			int v = event.getValue();
			
			initInstance(id);
			
			if (rts.get(id) > ts || wts.get(id) > ts) {
				trigger(new Pp2pSend(source, new NackMessage(self, id)), pp2p);
			}
			else {
				val.put(id, v);
				wts.put(id, ts);
				trigger(new Pp2pSend(source, new WriteAck(self, id, ts)), pp2p);
			}
		}
	};

	Handler<WriteAck> handleWriteAckDeliver = new Handler<WriteAck>() {
		public void handle(WriteAck event) {
			int id = event.getId();
			long sentts = event.getTs();
			
			if (sentts == tstamp.get(id)) {
				Integer tmp = wAcks.get(id) +1;
				wAcks.put(id, tmp);
				
				if (wAcks.get(id) == majority) {
					readSet.put(id, new HashSet<RWAC.Read>());
					wAcks.put(id, 0);
					trigger(new AcDecide(id, tempValue.get(id)), ac);
				}
			}
		}
	};
}