package org.fincl.miss.service.biz.bicycle.service;

import java.util.ArrayList;
import java.util.Map;

import org.fincl.miss.service.biz.bicycle.common.CommonVo;
import org.fincl.miss.service.biz.bicycle.vo.AdminMountingRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.AdminMoveRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.BikeRentInfoVo;
import org.fincl.miss.service.biz.bicycle.vo.RentHistVo;
import org.fincl.miss.service.biz.bicycle.vo.RentalRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.SerialNumberRentalRequestVo;

public interface BicycleRentService {


	public boolean rentProcUpdate(CommonVo com, Map<String, Object> userInfo);
	

	public boolean reservationInsert(CommonVo com, BikeRentInfoVo info);

	public BikeRentInfoVo getVoucher(CommonVo com);

	public Map<String, Object> getUseBikeInfo(CommonVo com);

	public void updateLockTEst(CommonVo com);

	public RentHistVo getForReturnUse(CommonVo com);

	public Map<String, Object> getOverFeePolicy(Map<String, Object> info);

	public void procReturn(RentHistVo info);

	public int getNoParkingRock(CommonVo com);

	public Map<String, Object> getRentHist(CommonVo com);

	public void insertRentMoveInfo(Map<String, Object> hist, ArrayList<Integer> lat, ArrayList<Integer> lon, int packNum);

	public void procAdminMove(CommonVo com);

	public boolean procAdminMounting(CommonVo com, AdminMountingRequestVo vo);

	public Map<String, Object> getOverFeeMaxPolicy(Map<String, Object> fee);

	public Map<String, Object> getOverFeeMinPolicy(Map<String, Object> fee);

	public int getUserWeight(String usr_SEQ);

	Map<String, Object> getRentMsgInfo(CommonVo com);
	
	void removeRelocateHist(CommonVo com);
	
	Map<String, Object> selectCascadParkingRock(CommonVo info);

	void deleteParkingInfoOnly(RentHistVo info);
	
	void deleteParkingInfoCascade(RentHistVo info);

	public void insertParkingInfo(RentHistVo info);

	public void insertBikeLocation(RentHistVo info);

	public void updateBike(RentHistVo info);
	
	void deleteDuplicatedParkingInfo(RentHistVo info);
	
	void deleteDuplicatedCascadeParkingInfo(RentHistVo info);
	
	void insertPeriodParkingInfo(RentHistVo info);
	
	void insertPeriodBikeLocation(RentHistVo info);
	
	void insertInvalidRentHist(RentHistVo info);
	
	RentHistVo checkInvalidRentInfo(CommonVo info);
	
	void deleteRentInfo(RentHistVo info);
	
	void setLastChkTime(RentalRequestVo info);
	
	void insertPeriodInfo(RentalRequestVo info);
	
	Map<String, String> chkOurBike(CommonVo com);
	
	Map<String, String> getSerialNumberInfo(SerialNumberRentalRequestVo info);
	
	public BikeRentInfoVo getUseVoucherInfo(CommonVo com);
	
	public String getLanguageCode(CommonVo com);
	
	Map<String, String> getBikeFirmwareVersion(CommonVo com);
	
	//T-APP PATCH VOUCHER UPDATE PROC ADD 2019.12.05
	void updatevoucherTAPP_2MIN_UNDER(String Voucher_seq);
	//T-APP PATCH VOUCHER UPDATE PROC ADD 2019.12.05
	void updatevoucherTAPP_2MIN_OVER(String Voucher_seq);
}
