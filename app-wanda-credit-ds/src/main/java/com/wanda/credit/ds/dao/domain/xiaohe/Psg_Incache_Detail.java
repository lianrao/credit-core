package com.wanda.credit.ds.dao.domain.xiaohe;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.wanda.credit.base.domain.BaseDomain;

/* 错误信息表 */
@Entity
@Table(name = "T_DS_JZ_INCACHEDETAIL")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XStreamAlias("psgIncacheDetail")
public class Psg_Incache_Detail extends BaseDomain {// 建表
	private static final long serialVersionUID = -1289807774918590425L;
	private String id;
	private String FlightCount;
	private String FlightDomesticCount;
	private String AvgDomesticPrice;
	private String AvgDomesticDiscount;
	private String FlightInterCount;
	private String AvgInterPrice;
	private String FavouriteAirline;
	private String FavouriteAirlineCount;
	private String FavouriteDest;
	private String FavouriteDestCount;
	private String DomesticFirstCount;
	private String DomesticBusCount;
	private String DomesticEcoCount;
	private String InterFirstCount;
	private String InterBusCount;
	private String InterEcoCount;
	private String BusOutCount;
	private String TravelOutCount;
	private String VisitOutCount;
	private String AvgDelayTime;
	private String DelayCount;
	private String BigFlightCount;
	private String SmallFlightCount;
	private Psg_IncacheMain psgincache;
	
	public Psg_Incache_Detail() {
		super();
	}

	public Psg_Incache_Detail(String id, String FlightCount, String FlightDomesticCount,String AvgDomesticPrice,String AvgDomesticDiscount,
			String FlightInterCount,String AvgInterPrice,String FavouriteAirline,String FavouriteAirlineCount,String FavouriteDest,
			String FavouriteDestCount,String DomesticFirstCount,String DomesticBusCount,String DomesticEcoCount,String InterFirstCount,
			String InterBusCount,String InterEcoCount,String BusOutCount,String TravelOutCount,String VisitOutCount,String AvgDelayTime,
			String DelayCount,String BigFlightCount,String SmallFlightCount,Psg_IncacheMain psgincache) {
		super();
		this.id = id;
		this.FlightCount = FlightCount;
		this.FlightDomesticCount = FlightDomesticCount;
		this.AvgDomesticPrice = AvgDomesticPrice;
		this.AvgDomesticDiscount = AvgDomesticDiscount;
		this.FlightInterCount = FlightInterCount;
		this.AvgInterPrice = AvgInterPrice;
		this.FavouriteAirline = FavouriteAirline;
		this.FavouriteAirlineCount = FavouriteAirlineCount;
		this.FavouriteDest = FavouriteDest;
		this.FavouriteDestCount = FavouriteDestCount;
		this.DomesticFirstCount = DomesticFirstCount;
		this.DomesticBusCount = DomesticBusCount;
		this.DomesticEcoCount = DomesticEcoCount;
		this.InterFirstCount = InterFirstCount;
		this.InterBusCount = InterBusCount;
		this.InterEcoCount = InterEcoCount;
		this.BusOutCount = BusOutCount;
		this.TravelOutCount = TravelOutCount;
		this.VisitOutCount = VisitOutCount;
		this.AvgDelayTime = AvgDelayTime;
		this.DelayCount = DelayCount;
		this.BigFlightCount = BigFlightCount;
		this.SmallFlightCount = SmallFlightCount;
		this.psgincache = psgincache;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "ID", unique = true, nullable = false, length = 32)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNERID")
	public Psg_IncacheMain getPsgincache() {
		return psgincache;
	}

	public void setPsgincache(Psg_IncacheMain psgincache) {
		this.psgincache = psgincache;
	}

	public String getFlightCount() {
		return FlightCount;
	}

	public void setFlightCount(String flightCount) {
		FlightCount = flightCount;
	}

	public String getFlightDomesticCount() {
		return FlightDomesticCount;
	}

	public void setFlightDomesticCount(String flightDomesticCount) {
		FlightDomesticCount = flightDomesticCount;
	}

	public String getAvgDomesticPrice() {
		return AvgDomesticPrice;
	}

	public void setAvgDomesticPrice(String avgDomesticPrice) {
		AvgDomesticPrice = avgDomesticPrice;
	}

	public String getAvgDomesticDiscount() {
		return AvgDomesticDiscount;
	}

	public void setAvgDomesticDiscount(String avgDomesticDiscount) {
		AvgDomesticDiscount = avgDomesticDiscount;
	}

	public String getFlightInterCount() {
		return FlightInterCount;
	}

	public void setFlightInterCount(String flightInterCount) {
		FlightInterCount = flightInterCount;
	}

	public String getAvgInterPrice() {
		return AvgInterPrice;
	}

	public void setAvgInterPrice(String avgInterPrice) {
		AvgInterPrice = avgInterPrice;
	}

	public String getFavouriteAirline() {
		return FavouriteAirline;
	}

	public void setFavouriteAirline(String favouriteAirline) {
		FavouriteAirline = favouriteAirline;
	}

	public String getFavouriteAirlineCount() {
		return FavouriteAirlineCount;
	}

	public void setFavouriteAirlineCount(String favouriteAirlineCount) {
		FavouriteAirlineCount = favouriteAirlineCount;
	}

	public String getFavouriteDest() {
		return FavouriteDest;
	}

	public void setFavouriteDest(String favouriteDest) {
		FavouriteDest = favouriteDest;
	}

	public String getFavouriteDestCount() {
		return FavouriteDestCount;
	}

	public void setFavouriteDestCount(String favouriteDestCount) {
		FavouriteDestCount = favouriteDestCount;
	}

	public String getDomesticFirstCount() {
		return DomesticFirstCount;
	}

	public void setDomesticFirstCount(String domesticFirstCount) {
		DomesticFirstCount = domesticFirstCount;
	}

	public String getDomesticBusCount() {
		return DomesticBusCount;
	}

	public void setDomesticBusCount(String domesticBusCount) {
		DomesticBusCount = domesticBusCount;
	}

	public String getDomesticEcoCount() {
		return DomesticEcoCount;
	}

	public void setDomesticEcoCount(String domesticEcoCount) {
		DomesticEcoCount = domesticEcoCount;
	}

	public String getInterFirstCount() {
		return InterFirstCount;
	}

	public void setInterFirstCount(String interFirstCount) {
		InterFirstCount = interFirstCount;
	}

	public String getInterBusCount() {
		return InterBusCount;
	}

	public void setInterBusCount(String interBusCount) {
		InterBusCount = interBusCount;
	}

	public String getInterEcoCount() {
		return InterEcoCount;
	}

	public void setInterEcoCount(String interEcoCount) {
		InterEcoCount = interEcoCount;
	}

	public String getBusOutCount() {
		return BusOutCount;
	}

	public void setBusOutCount(String busOutCount) {
		BusOutCount = busOutCount;
	}

	public String getTravelOutCount() {
		return TravelOutCount;
	}

	public void setTravelOutCount(String travelOutCount) {
		TravelOutCount = travelOutCount;
	}

	public String getVisitOutCount() {
		return VisitOutCount;
	}

	public void setVisitOutCount(String visitOutCount) {
		VisitOutCount = visitOutCount;
	}

	public String getAvgDelayTime() {
		return AvgDelayTime;
	}

	public void setAvgDelayTime(String avgDelayTime) {
		AvgDelayTime = avgDelayTime;
	}

	public String getDelayCount() {
		return DelayCount;
	}

	public void setDelayCount(String delayCount) {
		DelayCount = delayCount;
	}

	public String getBigFlightCount() {
		return BigFlightCount;
	}

	public void setBigFlightCount(String bigFlightCount) {
		BigFlightCount = bigFlightCount;
	}

	public String getSmallFlightCount() {
		return SmallFlightCount;
	}

	public void setSmallFlightCount(String smallFlightCount) {
		SmallFlightCount = smallFlightCount;
	}

	@Override
	public String toString() {
		return "Psg_Detail [FlightDomesticCount=" + this.FlightDomesticCount + ", FlightDomesticCount=" + this.FlightDomesticCount + "]";
	}
}
