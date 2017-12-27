package com.gimplatform.core.common;

/**
 * 版本号
 * @author zzd
 *
 */
public class VersionCode {

	public String verOne;
	
	public String verTwo;
	
	public String verThree;
	
	public int position;
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public VersionCode(String verOne, String verTwo, String verThree) {
		this.verOne = verOne;
		this.verTwo = verTwo;
		this.verThree = verThree;
	}
	
	public String getVerOne() {
		return verOne;
	}
	
	public void setVerOne(String verOne) {
		this.verOne = verOne;
	}
	
	public String getVerTwo() {
		return verTwo;
	}
	
	public void setVerTwo(String verTwo) {
		this.verTwo = verTwo;
	}
	
	public String getVerThree() {
		return verThree;
	}
	
	public void setVerThree(String verThree) {
		this.verThree = verThree;
	}
}
