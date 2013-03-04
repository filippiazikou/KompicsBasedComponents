package se.kth.ict.id2203.lpb;

import se.kth.ict.id2203.flp2p.Flp2pDeliver;
import se.sics.kompics.address.Address;

public class RequestMessage extends Flp2pDeliver{

	private static final long serialVersionUID = 2193713942080123560L;
	
	private Address source;
	private Address datasource;
	private int missing;
	private int r;
	
	protected RequestMessage(Address source, Address datasource, int missing, int r) {
		super(source);
		this.datasource =datasource;
		this.source = source;
		this.missing = missing;
		this.r = r;
	}


	public int getMissing() {
		return missing;
	}

	public int getR() {
		return r;
	}

	public Address getDatasource() {
		return datasource;
	}
	

}
