package kosta.mapda.service.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kosta.mapda.domain.service.Coupon;
import kosta.mapda.domain.service.CouponCategory;
import kosta.mapda.repository.CouponCategoryRepository;
import kosta.mapda.repository.CouponRepository;

@Service
@Transactional
public class CouponServiceImpl implements CouponService {

	@Autowired
	private CouponRepository couponRepository;
	
	@Autowired
	private CouponCategoryRepository couponCategoryRepository;
	
	@Override
	public Page<Coupon> selectAll(Pageable pageable, String couponName, Long category) {
		Page<Coupon> couponResult;
		if(couponName==null && category==null) {
			couponResult = couponRepository.findAll(pageable);
		}else if(couponName!=null && category==null){
			couponResult = this.selectByName(pageable, couponName);
		}else if(couponName==null && category!=null) {
			couponResult = this.selectByCategory(pageable, category);
		}else {
			CouponCategory couponCategory = couponCategoryRepository.findById(category).orElse(null);
			couponResult = couponRepository.findAllBycpNameContainingAndcouponCategory(pageable, couponName, couponCategory);
		}
			
		return couponResult;
	}

	@Override
	public Coupon selectCoupon(Long couponNo) {
		
		return couponRepository.findById(couponNo).orElse(null);
	}

	@Override
	public Page<Coupon> selectByName(Pageable pageable, String couponName) {
		
		return couponRepository.findBycpNameContaining(pageable, couponName);
		
	}

	@Override
	public Page<Coupon> viewAll(Pageable pageable) {
		
		return couponRepository.findAll(pageable);
	}
	
	/**
	 * 발급상태 변경하는 ajax 메소드
	 */
	@Override
	public int stop(Long cpNo) {
		return couponRepository.stop(cpNo);
	}

	@Override
	public Page<Coupon> selectByCategory(Pageable pageable, Long category) {
		
		CouponCategory couponCategory = couponCategoryRepository.findById(category).orElse(null);
		
		return couponRepository.findBycouponCategory(pageable, couponCategory);
		
	}

	@Override
	public List<CouponCategory> couponCategory() {
		
		return couponCategoryRepository.findAll();
	}
	
}
