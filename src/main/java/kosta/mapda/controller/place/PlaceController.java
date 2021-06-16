package kosta.mapda.controller.place;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import kosta.mapda.domain.Management;
import kosta.mapda.domain.map.Place;
import kosta.mapda.domain.map.PlacePhoto;
import kosta.mapda.domain.map.PlacePhotoReview;
import kosta.mapda.domain.map.PlaceReview;
import kosta.mapda.domain.member.Member;
import kosta.mapda.service.place.PlaceReviewService;
import kosta.mapda.service.place.PlaceService;

@Controller
@RequestMapping("/place")
public class PlaceController {

	@Autowired
	private PlaceService placeService;
	
	@Autowired
	private PlaceReviewService prService;
	
	private final String SAVE_PATH = "C:\\KostaEdu\\thirdProject\\fileSave";
	
	private LocalDateTime ldt;
	
	/**
	 * 		상세보기
	 * */
	@RequestMapping("/read/{placeNo}")
	public ModelAndView read(@PathVariable Long placeNo) {
		int starAvg=0;
		int starAvgPer=0;
		
		Place place = placeService.selectBy(placeNo);
		List<PlacePhoto> ppList = placeService.selectAllPlacePhoto(placeNo);
		List<PlaceReview> prList = prService.selectAllPlaceReview(placeNo);
		List<PlacePhotoReview> pprList = prService.selectAllPhotoReview(placeNo);
		
		//포토리뷰 평점 구하기
		for(PlacePhotoReview ppr : pprList) {
			System.out.println("ppr.getPprStar() = " + ppr.getPprStar());
			starAvg+=ppr.getPprStar();
		}
		
		if(pprList.size()==0) {
			starAvg=0;
		}else {
			starAvg=starAvg/pprList.size();
		}
		
		starAvgPer=starAvg*10;
				
		
		// 해쉬태그 #기준으로 나눔
		List<String> tagStr=new ArrayList<String>();
		String str = place.getPlaceTag();
		String [] hashStr = str.split("#");
		for(int i=1; i<hashStr.length; i++) {
			tagStr.add(hashStr[i]);
		}
		
		//총 리뷰수
		int totalReviewCount=(prList.size() + pprList.size());
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("place/read");
		
		mv.addObject("ppList", ppList);
		mv.addObject("place", place);
		mv.addObject("tagStr",tagStr);
		mv.addObject("prList", prList);
		mv.addObject("pprList", pprList);
		mv.addObject("starAvg", starAvg);
		mv.addObject("starAvgPer", starAvgPer);
		mv.addObject("totalReviewCount", totalReviewCount);
		
		return mv;
	}
	
	/**
	 * 		댓글 등록하기
	 * */
	@RequestMapping("/replyWrite")
	public String insert(PlaceReview placeReview, Long placeNo, Member member, Management management,HttpServletRequest request) {
		Long memNo =Long.valueOf(request.getParameter("memNo"));
		Long mngNo = Long.valueOf(request.getParameter("mngNo"));
		System.out.println("placeReview.getPrStatus()" + placeReview.getPrStatus());
		placeReview.setPlace(new Place(placeNo));
		placeReview.setMember(new Member(memNo));
		placeReview.setManagement(new Management(mngNo));

		prService.insert(placeReview);
		return "redirect:/place/read/"+placeNo;
	}
	
	/**
	 * 		내가 작성한 댓글 / 포토후기
	 * */
	@RequestMapping("/myReplyReview/placeNo={placeNo}&memId={memId}")
	public ModelAndView myReplyReview(@PathVariable Long placeNo, @PathVariable String memId) {
		 List<PlaceReview> prList= prService.selectByPlaceNoMemId(placeNo, memId);
		 List<PlacePhotoReview> pprList = prService.selectByMemIdPlaceNo(placeNo, memId);
		 Place place = placeService.selectBy(placeNo);
		 
		 ModelAndView mv = new ModelAndView();
		mv.setViewName("place/myReplyReview");
		mv.addObject("prList", prList);
		mv.addObject("pprList", pprList);
		mv.addObject("place", place);
		
		return mv;
	}
	
	/**
	 * 	 내 댓글 삭제하기
	 * */
	@RequestMapping("/placeReplyDelete/placeNo={placeNo}&memId={memId}/{prNo}")
	public String placeReplyDelete(@PathVariable Long placeNo, @PathVariable String memId,@PathVariable Long prNo) {
		prService.delete(prNo);
		return "redirect:/place/myReplyReview/placeNo="+placeNo+"&memId="+memId;
	}
	/**
	 * 		내 포토후기 삭제하기
	 * */
	@RequestMapping("/photoReviewDelete/placeNo={placeNo}&memId={memId}/{pprNo}")
	public String photoReviewDelete(@PathVariable Long placeNo, @PathVariable String memId, @PathVariable Long pprNo) {
		prService.prDelete(pprNo);
		return "redirect:/place/myReplyReview/placeNo="+placeNo+"&memId="+memId;
	}
	
	/**
	 * 		내 댓글 수정하기
	 * */
	@RequestMapping("/placeReplyUpdate/placeNo={placeNo}&memId={memId}/{prNo}")
	public String placeReplyUpdate(@PathVariable Long placeNo, @PathVariable String memId,@PathVariable Long prNo, PlaceReview placeReview) {
		PlaceReview dbPlaceReview = prService.prUpdate(placeReview);
//		System.out.println("placeReview = " + placeReview.getPrNo());
//		System.out.println("dbPlaceReview.getPrNo() = " +dbPlaceReview.getPrNo());
//		System.out.println("dbPlaceReview.getPrContent() = "+dbPlaceReview.getPrContent());
		return "redirect:/place/myReplyReview/placeNo="+placeNo+"&memId="+memId;
	}
	
	/**
	 * 		내 포토후기 수정하기
	 * */
	@RequestMapping("/placePhotoReviewUpdate/placeNo={placeNo}&memId={memId}/{pprNo}")
	public String placePhotoReviewUpdate(@PathVariable Long placeNo, @PathVariable String memId,@PathVariable Long pprNo, PlacePhotoReview placePhotoReview) {
		PlacePhotoReview dbPpr = prService.prrUpdate(placePhotoReview);
//		System.out.println("placePhotoReview = " + placePhotoReview.getPprNo());
//		System.out.println("dbPpr.getPprNo() = " +dbPpr.getPprNo());
//		System.out.println("dbPpr.getPprContent() = "+dbPpr.getPprContent());
		return "redirect:/place/myReplyReview/placeNo="+placeNo+"&memId="+memId;
	}
	
	/**
	 * 		플레이스 등록하기 폼
	 * */
	@RequestMapping("/placeInsertForm")
	public String placeInsertForm() {
		return "place/placeInsertForm";
	}
	

	
}
