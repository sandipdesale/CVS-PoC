package com.cognizant.cvs.poc.routing_rules;

/**
 * This class was automatically generated by the data modeler tool.
 */

public class WorkItem implements java.io.Serializable {

    static final long serialVersionUID = 1L;

	private java.lang.String clientId;

	private java.lang.String id;

	private java.lang.String status;

	private java.util.List<com.cognizant.cvs.poc.routing_rules.LineItem> lineItems;

	private java.lang.String state;

	private java.lang.Boolean certificationNeeded;

	public java.lang.String getClientId() {
        return this.clientId;
    }
    
    public void setClientId(java.lang.String clientId) {
        this.clientId = clientId;
    }

    public java.lang.String getId() {
        return this.id;
    }
    
    public void setId(java.lang.String id) {
        this.id = id;
    }

	public java.lang.String getStatus() {
		return this.status;
	}

	public void setStatus(java.lang.String status) {
		this.status = status;
	}

	public java.util.List<com.cognizant.cvs.poc.routing_rules.LineItem> getLineItems() {
		return this.lineItems;
	}

	public void setLineItems(
			java.util.List<com.cognizant.cvs.poc.routing_rules.LineItem> lineItems) {
		this.lineItems = lineItems;
	}

	public java.lang.String getState() {
		return this.state;
	}

	public void setState(java.lang.String state) {
		this.state = state;
	}

	public java.lang.Boolean getCertificationNeeded() {
		return this.certificationNeeded;
	}

	public void setCertificationNeeded(java.lang.Boolean certificationNeeded) {
		this.certificationNeeded = certificationNeeded;
	}

	public WorkItem() {
	}

	public WorkItem(
			java.lang.String id,
			java.lang.String clientId,
			java.lang.String status,
			java.util.List<com.cognizant.cvs.poc.routing_rules.LineItem> lineItems,
			java.lang.String state, java.lang.Boolean certificationNeeded) {
		this.id = id;
		this.clientId = clientId;
		this.status = status;
		this.lineItems = lineItems;
		this.state = state;
		this.certificationNeeded = certificationNeeded;
	}

	@Override
	public String toString() {
		return "WorkItem [clientId=" + clientId + ", id=" + id + ", status=" + status + ", lineItems=" + lineItems
				+ ", state=" + state + ", certificationNeeded=" + certificationNeeded + "]";
	}
	
	
	

}