package org.fincl.miss.service.biz.bicycle.vo;

public class BikeRentInfoVo {
	
	private String  usr_seq;
	private String  voucher_seq;
	private String  rent_station_id;
	private String  rent_rack_id;
	private String  rent_bike_id;
	private String  rent_cls_cd;
	private String  cascade_yn;
	private String  now_locate_id;
	
	
	public String getCascade_yn() {
		return cascade_yn;
	}
	public void setCascade_yn(String cascade_yn) {
		this.cascade_yn = cascade_yn;
	}
	public String getNow_locate_id() {
		return now_locate_id;
	}
	public void setNow_locate_id(String now_locate_id) {
		this.now_locate_id = now_locate_id;
	}
	public String getUsr_seq() {
		return usr_seq;
	}
	public void setUsr_seq(String usr_seq) {
		this.usr_seq = usr_seq;
	}
	public String getVoucher_seq() {
		return voucher_seq;
	}
	public void setVoucher_seq(String voucher_seq) {
		this.voucher_seq = voucher_seq;
	}
	public String getRent_station_id() {
		return rent_station_id;
	}
	public void setRent_station_id(String rent_station_id) {
		this.rent_station_id = rent_station_id;
	}
	public String getRent_rack_id() {
		return rent_rack_id;
	}
	public void setRent_rack_id(String rent_rack_id) {
		this.rent_rack_id = rent_rack_id;
	}
	public String getRent_bike_id() {
		return rent_bike_id;
	}
	public void setRent_bike_id(String rent_bike_id) {
		this.rent_bike_id = rent_bike_id;
	}
	public String getRent_cls_cd() {
		return rent_cls_cd;
	}
	public void setRent_cls_cd(String rent_cls_cd) {
		this.rent_cls_cd = rent_cls_cd;
	}
	

	
}
