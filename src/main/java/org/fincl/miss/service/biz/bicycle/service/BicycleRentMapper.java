package org.fincl.miss.service.biz.bicycle.service;

import java.util.Map;

import org.fincl.miss.service.biz.bicycle.common.CommonVo;
import org.fincl.miss.service.biz.bicycle.common.QRLogVo;
import org.fincl.miss.service.biz.bicycle.vo.QRAdminMountingRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.AdminMoveRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.BikeRentInfoVo;
import org.fincl.miss.service.biz.bicycle.vo.RentHistVo;
import org.fincl.miss.service.biz.bicycle.vo.RentalRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.SerialNumberRentalRequestVo;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

@Mapper("BicycleRentMapper")
public interface BicycleRentMapper {

	void rentTableUpdate(CommonVo com);
	
	void rentTableUpdate2(CommonVo com);
	
	void voucherTableUpdate(Map<String, Object> voucher);

	void rentBikeUpdate(CommonVo com);

	void rentBikeLocationUpdate(CommonVo com);

	void parkingInfoDelete(CommonVo com);

	void rentTableInsert(BikeRentInfoVo bikeInfo);
	
	BikeRentInfoVo getVoucher(CommonVo com);
	
	BikeRentInfoVo getNotUseVoucherInfo(CommonVo com);

	BikeRentInfoVo getBikeInfo(CommonVo com);

	Map<String, Object> getVoucherInfo(CommonVo com);

	Map<String, Object> getUseBikeInfo(CommonVo com);
	
	Map<String, Object> getUseBikeInfoFULL(CommonVo com);

	void lockTableUpdate(CommonVo com);

	void updateLockTEst(CommonVo com);

	RentHistVo getForReturnUse(CommonVo com);

	Map<String, Object> getOverFeePolicy(Map<String, Object> info);

	void insertRentHist(RentHistVo info);
	
	void updateRentHist(RentHistVo info);

	void insertRentOverFee(RentHistVo info);

	void insertParkingInfo(RentHistVo info);

	void deleteParkingInfo(RentHistVo info);
	
	void updateBike(RentHistVo info);
	
	void updateBikeGPS(RentHistVo info);
	
	void updateBike_NOGPS(RentHistVo info);

	void updateBikeBreakDowon(RentHistVo info);
	
	void insertBikeLocation(RentHistVo info);

	void deleteRentInfo(RentHistVo info);
	void deleteRentInfo_rserved(RentHistVo info);
	
	int getNoParkingRock(CommonVo com);

	Map<String, Object> getRentHist(CommonVo com);

	void insertRentMoveInfo(Map<String, Object> hist);

	Map<String, Object> getOverFeeMaxPolicy(Map<String, Object> fee);

	Map<String, Object> getOverFeeMinPolicy(Map<String, Object> fee);

	int getUserWeight(String usr_SEQ);

	void updateBikeLocation(RentHistVo info);
	
	Map<String, Object> getRentMsgInfo(CommonVo com);
	
	Map<String, Object> getRentMsgInfo2(CommonVo com);

	//int checkRelocateHist(Map<String, Object> hist);
	
	void insertRelocateHist(CommonVo com);
	
	void replaceRelocateHist(CommonVo com);
	
	void updateRelocateHist(CommonVo com);
	
	void removeRelocateHist(CommonVo com);
	
	Map<String, Object> selectCascadParkingRock(CommonVo info);
	
	void deleteParkingInfoOnly(RentHistVo info);
	
	void deleteParkingInfoCascade(RentHistVo info);
	
	void deleteDuplicatedParkingInfo(RentHistVo info);
	
	void deleteDuplicatedCascadeParkingInfo(RentHistVo info);
	
	void updateDeviceState(RentHistVo info);
	
	void insertPeriodParkingInfo(RentHistVo info);
	
	void insertPeriodBikeLocation(RentHistVo info);
	
	void insertInvalidRentHist(RentHistVo info);
	
	RentHistVo checkInvalidRentInfo(CommonVo info);
	
	Map<String, Object> getRentVoucherInfo(CommonVo com);
	
	String getStationNo (String RETURN_RACK_ID);
	
	String getStationName (String RETURN_RACK_ID);
	
	void setLastChkTime(RentalRequestVo info);
	
	void insertPeriodInfo(RentalRequestVo info);
	
	String getPolicyOpenYn(String MILEAGE_POLICY_OPEN_CD);	// 반납 => 탄소절감량 마일리지 정책 Open 여부_20170112
	
	String getMemberYn(RentHistVo info);	// 반납 => 정회원 여부_20170112
	
	Map<String, String> getSaveCarbonStationInfo(RentHistVo info);	// 탄소절감 등록 대여/반납 대여소 정보 가져오기_20170121
	
	RentHistVo getSaveCarbonInfoCompare(RentHistVo info);	// 반납 => 회원 별 탄소절감 정보 조회 및 대여/반납 대여소 마일리지 적용여부 확인_20170112
	
	Map<String, String> getMileageMaxPoint(RentHistVo info);
	
	void insertSaveCarbonMileage(RentHistVo info);	// 반납 => 탄소절감량 마일리지 정보 삽입_20170113
	
	void setRecommendTimePolicy(CommonVo com);	// 추천반납대여소 시간별 정책추출 로직_20170213
	
	String getRecommendMileagePolicyOpenYn();	// 추천반납대여소 마일리지 정책추출 로직_20170221
	
	String getRentHistSeq(RentHistVo info);
	
	String getReturnRank(RentHistVo info);
	
	void updateRecommendInfo(RentHistVo info);
	
	String getRentSeq(RentHistVo info);
	
	RentHistVo getRecommendStationInfoCompare(RentHistVo info);
	
	String getRecommendExistsYn(RentHistVo info);
	
	Map<String, String> chkOurBike(CommonVo com);
	
	Map<String, String> getSerialNumberInfo(SerialNumberRentalRequestVo info);
	
	BikeRentInfoVo getUseVoucherInfo(CommonVo com);
	
	String getLanguageCode(CommonVo com);
	
	Map<String, String> getBikeFirmwareVersion(CommonVo com);
	
	void insertQRLog(QRLogVo QRLog);
	
	void updateQRLog(QRLogVo QRLog);
	
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
	
	void rentCancleInsert(String RENT_SEQ);
	
	void rentDelete(String RENT_SEQ);
	
	void updateRENTGPS_CLS_CD(Map<String, String> GPS_DATA);
}

