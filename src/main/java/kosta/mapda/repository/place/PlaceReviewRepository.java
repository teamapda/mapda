package kosta.mapda.repository.place;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kosta.mapda.domain.map.PlaceReview;

public interface PlaceReviewRepository extends JpaRepository<PlaceReview, Long> {
	@Query("select pr from PlaceReview pr where pr.place.placeNo=?1 order by pr.prRegdate")
	List<PlaceReview> selectByPlaceNo(Long placeNo);
	
	@Query("select pr from PlaceReview pr where pr.place.placeNo=?1 and pr.member.memId=?2")
	List<PlaceReview> selectByPlaceNoMemId(Long placeNo, String memId);

	@Query("select count(p) from PlaceReview p where p.prRegdate between ?1 and ?2")
	int getReviewDateBetween(LocalDateTime startDatetime, LocalDateTime endDatetime);
}