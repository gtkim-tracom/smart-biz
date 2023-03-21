package org.fincl.miss.service.biz.bicycle.service;

import java.util.ArrayList;
import java.util.Map;

import org.fincl.miss.service.biz.bicycle.common.CommonVo;
import org.fincl.miss.service.biz.bicycle.common.QRLogVo;
import org.fincl.miss.service.biz.bicycle.vo.QRAdminMountingRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.AdminMoveRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.BikeRentInfoVo;
import org.fincl.miss.service.biz.bicycle.vo.RentHistVo;
import org.fincl.miss.service.biz.bicycle.vo.RentalRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.SerialNumberRentalRequestVo;

public interface BicycleRentService {


	public boolean rentProcUpdate(CommonVo com, Map<String, Object> userInfo);
	
	public boolean rentUpdateCancel(String RENT_SEQ);

	public boolean reservationInsert(CommonVo com, BikeRentInfoVo info);

	public BikeRentInfoVo getVoucher(CommonVo com);

	public Map<String, Object> getUseBikeInfo(CommonVo com);
	
	public Map<String, Object> getUseBikeInfoFULL(CommonVo com);

	public void updateLockTEst(CommonVo com);

	public RentHistVo getForReturnUse(CommonVo com);

	public Map<String, Object> getOverFeePolicy(Map<String, Object> info);

	public void procReturn(RentHistVo info);

	public int getNoParkingRock(CommonVo com);

	public Map<String, Object> getRentHist(CommonVo com);

	public void insertRentMoveInfo(Map<String, Object> hist, ArrayList<Integer> lat, ArrayList<Integer> lon, int packNum);

	public void procAdminMove(CommonVo com);

	public boolean procAdminMounting(CommonVo com, QRAdminMountingRequestVo vo);

	public Map<String, Object> getOverFeeMaxPolicy(Map<String, Object> fee);

	public Map<String, Object> getOverFeeMinPolicy(Map<String, Object> fee);

	public int getUserWeight(String usr_SEQ);

	Map<String, Object> getRentMsgInfo(CommonVo com);
	
	Map<String, Object> getRentMsgInfo2(CommonVo com);
	
	void removeRelocateHist(CommonVo com);
	
	Map<String, Object> selectCascadParkingRock(CommonVo info);

	void deleteParkingInfoOnly(RentHistVo info);
	
	void deleteParkingInfoCascade(RentHistVo info);
	
	void parkingInfoDelete(CommonVo com);

	public void insertParkingInfo(RentHistVo info);

	public void insertBikeLocation(RentHistVo info);

	public void updateBike(RentHistVo info);
	
	public void updateBikeGPS(RentHistVo info);
	
	public void updateBike_NOGPS(RentHistVo info);
	
	void deleteDuplicatedParkingInfo(RentHistVo info);
	
	void deleteDuplicatedCascadeParkingInfo(RentHistVo info);
	
	void insertPeriodParkingInfo(RentHistVo info);
	
	void insertPeriodBikeLocation(RentHistVo info);
	
	void insertInvalidRentHist(RentHistVo info);
	
	RentHistVo checkInvalidRentInfo(CommonVo info);
	
	void deleteRentInfo(RentHistVo info);
	
	void deleteRentInfo_rserved(RentHistVo info);
	
	void setLastChkTime(RentalRequestVo info);
	
	void insertPeriodInfo(RentalRequestVo info);
	
	Map<String, String> chkOurBike(CommonVo com);
	
	Map<String, String> getSerialNumberInfo(SerialNumberRentalRequestVo info);
	
	public BikeRentInfoVo getUseVoucherInfo(CommonVo com);
	
	public String getLanguageCode(CommonVo com);
	
	Map<String, String> getBikeFirmwareVersion(CommonVo com);
	
	public void insertQRLog(QRLogVo QRLog);
	
	public void updateQRLog(QRLogVo QRLog);
	
	void updatevoucherTAPP_2MIN_UNDER(String Voucher_seq);
	
	void updatevoucherTAPP_2MIN_OVER(String Voucher_seq);
	
	double getBikeMoveDist(CommonVo com);
	
	double getBikeMoveDist_Last(Map<String, String> GPS_DATA);
	double getCalcDistance(Map<String, String> GPS_DATA);
	
	Map<String, Object> getBikeMoveDist_COUNT(Map<String, String> GPS_DATA);
	
	void insertRentGPSDATA(Map<String, String> GPS_DATA);
	
	void deleteRentGPSDATA(RentHistVo HistVo) ;
	
	void insertRentMove_Info(Map<String, String> GPS_DATA);
	
	void updateBikeCnt(String voucherseq);
	
	void updateKickCnt(String voucherseq);
	
	Map<String, String> getBikeRETURN_GPS(String RENT_SEQ);
	
	void updateRENTGPS_CLS_CD(Map<String, String> GPS_DATA);
	
}
