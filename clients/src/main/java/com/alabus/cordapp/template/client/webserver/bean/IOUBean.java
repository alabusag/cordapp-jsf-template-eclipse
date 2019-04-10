package com.alabus.cordapp.template.client.webserver.bean;

import lombok.Data;

/**
 * This class implements a default bean
 * 
 * @author zh10082
 */
@Data
public class IOUBean {

    private Integer value;
    private String otherPartyName;

    protected IOUBean() {
    }

    public IOUBean(Integer value, String otherPartyName) {
    	this.value = value;
    	this.otherPartyName = otherPartyName;
    }

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getOtherPartyName() {
		return otherPartyName;
	}
	
	public void setOtherPartyName(String otherPartyName) {
		this.otherPartyName = otherPartyName;
	}
    
    
}
