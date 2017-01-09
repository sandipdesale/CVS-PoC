package com.cognizant.cvs.poc.routing_rules;

/**
 * This class was automatically generated by the data modeler tool.
 */

public class Rph implements java.io.Serializable {

    static final long serialVersionUID = 1L;

	private java.lang.String certification;

	private java.lang.String id;

	private java.lang.String pharmacyId;

	private java.lang.String state;

	private java.util.List<com.cognizant.cvs.poc.routing_rules.WorkItem> workItems;

	private java.lang.Boolean shortlisted;

	public java.lang.String getCertification() {
        return this.certification;
    }
    
    public void setCertification(java.lang.String certification) {
        this.certification = certification;
    }

    public java.lang.String getId() {
        return this.id;
    }
    
    public void setId(java.lang.String id) {
        this.id = id;
    }

    public java.lang.String getPharmacyId() {
        return this.pharmacyId;
    }
    
    public void setPharmacyId(java.lang.String pharmacyId) {
        this.pharmacyId = pharmacyId;
    }

    public java.lang.String getState() {
        return this.state;
    }
    
    public void setState(java.lang.String state) {
        this.state = state;
    }

    public java.util.List<com.cognizant.cvs.poc.routing_rules.WorkItem> getWorkItems() {
        return this.workItems;
    }
    
    public void setWorkItems(java.util.List<com.cognizant.cvs.poc.routing_rules.WorkItem> workItems) {
        this.workItems = workItems;
    }

	public java.lang.Boolean getShortlisted() {
		return this.shortlisted;
	}

	public void setShortlisted(java.lang.Boolean shortlisted) {
		this.shortlisted = shortlisted;
	}

	public Rph() {
	}

	public Rph(
			java.lang.String id,
			java.lang.String state,
			java.lang.String certification,
			java.lang.String pharmacyId,
			java.util.List<com.cognizant.cvs.poc.routing_rules.WorkItem> workItems,
			java.lang.Boolean shortlisted) {
		this.id = id;
		this.state = state;
		this.certification = certification;
		this.pharmacyId = pharmacyId;
		this.workItems = workItems;
		this.shortlisted = shortlisted;
	}

	@Override
	public String toString() {
		return "Rph [certification=" + certification + ", id=" + id + ", pharmacyId=" + pharmacyId + ", state=" + state
				+ ", workItems=" + workItems + ", shortlisted=" + shortlisted + "]";
	}

	
	
}