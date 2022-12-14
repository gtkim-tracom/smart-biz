package org.fincl.miss.service.biz.bicycle.service;

import java.util.Map;

import org.fincl.miss.service.biz.bicycle.common.CommonVo;
import org.fincl.miss.service.biz.bicycle.vo.AdminMountingRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.AdminMoveRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.BikeRentInfoVo;
import org.fincl.miss.service.biz.bicycle.vo.RentHistVo;
import org.fincl.miss.service.biz.bicycle.vo.RentalRequestVo;
import org.fincl.miss.service.biz.bicycle.vo.SerialNumberRentalRequestVo;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

@Mapper("BicycleRentMapper")
public interface BicycleRentMapper {

	void rentTableUpdate(CommonVo com);
	
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

	void lockTableUpdate(CommonVo com);

	void updateLockTEst(CommonVo com);

	RentHistVo getForReturnUse(CommonVo com);

	Map<String, Object> getOverFeePolicy(Map<String, Object> info);

	void insertRentHist(RentHistVo info);

	void insertRentOverFee(RentHistVo info);

	void insertParkingInfo(RentHistVo info);

	void deleteParkingInfo(RentHistVo info);
	
	void updateBike(RentHistVo info);

	void updateBikeBreakDowon(RentHistVo info);
	
	void insertBikeLocation(RentHistVo info);

	void deleteRentInfo(RentHistVo info);

	int getNoParkingRock(CommonVo com);

	Map<String, Object> getRentHist(CommonVo com);

	void insertRentMoveInfo(Map<String, Object> hist);

	Map<String, Object> getOverFeeMaxPolicy(Map<String, Object> fee);

	Map<String, Object> getOverFeeMinPolicy(Map<String, Object> fee);

	int getUserWeight(String usr_SEQ);

	void updateBikeLocation(RentHistVo info);
	
	Map<String, Object> getRentMsgInfo(CommonVo com);

	int checkRelocateHist(Map<String, Object> hist);
	
	void insertRelocateHist(CommonVo com);
	
	void replaceRelocateHist(AdminMountingRequestVo info);
	
	void updateRelocateHist(AdminMountingRequestVo info);
	
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
	
	Map<String, String> getStationUSEYN (String RETURN_RACK_ID);
	
	void setLastChkTime(RentalRequestVo info);
	
	void insertPeriodInfo(RentalRequestVo info);
	
	String getPolicyOpenYn(String MILEAGE_POLICY_OPEN_CD);	// ?????? => ??????????????? ???????????? ?????? Open ??????_20170112
	
	String getMemberYn(RentHistVo info);	// ?????? => ????????? ??????_20170112
	
	Map<String, String> getSaveCarbonStationInfo(RentHistVo info);	// ???????????? ?????? ??????/?????? ????????? ?????? ????????????_20170121
	
	RentHistVo getSaveCarbonInfoCompare(RentHistVo info);	// ?????? => ?????? ??? ???????????? ?????? ?????? ??? ??????/?????? ????????? ???????????? ???????????? ??????_20170112
	
	Map<String, String> getMileageMaxPoint(RentHistVo info);
	
	void insertSaveCarbonMileage(RentHistVo info);	// ?????? => ??????????????? ???????????? ?????? ??????_20170113
	
	void setRecommendTimePolicy(CommonVo com);	// ????????????????????? ????????? ???????????? ??????_20170213
	
	String getRecommendMileagePolicyOpenYn();	// ????????????????????? ???????????? ???????????? ??????_20170221
	
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
	
	//T-APP PATCH VOUCHER UPDATE PROC ADD 2019.12.05
	void updatevoucherTAPP_2MIN_UNDER(String Voucher_seq);
	//T-APP PATCH VOUCHER UPDATE PROC ADD 2019.12.05
	void updatevoucherTAPP_2MIN_OVER(String Voucher_seq);
}
