/**   
* @Description: 政通高清人像比对服务
* @author wangjing
* @date 2019年01月4日 上午11:59:14 
* @version V1.0   
*/
package com.wanda.credit.ds.dao.domain.zhengtong;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.wanda.credit.base.domain.BaseDomain;

/**
 * @author wangjing
 */
@Entity
@Table(name = "T_DS_ZT_FACE_RESULT",schema="CPDB_DS")
@SequenceGenerator(name="SEQ_T_DS_ZT_FACE_RESULT",sequenceName="SEQ_T_DS_ZT_FACE_RESULT",allocationSize=1) 
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ZT_Face_Result extends BaseDomain{
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	private long id;
	private String trade_id;
	private String image;//照片ID
	
	private String name;
	private String certseq;//证件号
	private String respcd;//应答码
	private String respinfo;//应答信息
	private String mpssim;//公安比对分值
	private String status;//业务状态 "00：成功；	03：失败；"
	private String facePicMPS;
	private String localsim;
	private String sysSeqNb;
	private String telephone;
	
	private String error_no;//等于0成功，小于0代表失败
	private String error_info;//失败说明
	
//	[error_no]:[error_info]			[返回码（调用接口返回码）]:[失败说明]					"[0]:成功
//			[-1008]:机构信息异常
//			[-1009]:机构信息异常
//			[-1006]:非有效证件，请确保证件无污损，再重新尝试
//			[-20001]：网络不可用，请稍后再试；
//			[-2000000]:参数被篡改"								
//																			
//	[respcd]:[respinfo]			[应答码（查询接口）]:[应答信息]					"[1000]:认证一致（通过）
//			[1001]:认证不一致（不通过）
//			[1001]:认证不一致（不通过）:系统判断为不同人
//			[1001]:认证不一致（不通过）:姓名证件号匹配，请检查图片 
//			[1001]:认证不一致（不通过）:库中无照片
//			[1001]:认证不一致（不通过）:不能确定是否为同一人
//			[1001]:认证不一致（不通过）:姓名证件号不匹配 
//			[1002]:库中无此号
//			[1002]:交易异常
//			[1002]:交易异常:上传相片质量校验不合格，请重新拍摄上传
//			[1002]:交易异常:检测到多于一张人脸
//			[1002]:交易异常:照片base64编码异常
//			注: 若需根据比对分值做判断，则建议分值标准为：mpssim>=45确认为同一人；40<mpssim<45不确定为同一人；mpssim<=40确定为不同人；"								

	/**
	 * 获取 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_T_DS_ZT_FACE_RESULT")
	@Column(name = "ID", unique = true, nullable = false)
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTrade_id() {
		return trade_id;
	}
	public void setTrade_id(String trade_id) {
		this.trade_id = trade_id;
	}
	public String getCertseq() {
		return certseq;
	}
	public void setCertseq(String certseq) {
		this.certseq = certseq;
	}
	public String getRespcd() {
		return respcd;
	}
	public void setRespcd(String respcd) {
		this.respcd = respcd;
	}
	public String getRespinfo() {
		return respinfo;
	}
	public void setRespinfo(String respinfo) {
		this.respinfo = respinfo;
	}
	public String getMpssim() {
		return mpssim;
	}
	public void setMpssim(String mpssim) {
		this.mpssim = mpssim;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFacePicMPS() {
		return facePicMPS;
	}
	public void setFacePicMPS(String facePicMPS) {
		this.facePicMPS = facePicMPS;
	}
	public String getLocalsim() {
		return localsim;
	}
	public void setLocalsim(String localsim) {
		this.localsim = localsim;
	}
	public String getSysSeqNb() {
		return sysSeqNb;
	}
	public void setSysSeqNb(String sysSeqNb) {
		this.sysSeqNb = sysSeqNb;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getError_no() {
		return error_no;
	}
	public void setError_no(String error_no) {
		this.error_no = error_no;
	}
	public String getError_info() {
		return error_info;
	}
	public void setError_info(String error_info) {
		this.error_info = error_info;
	}
	@Override
	public String toString() {
		return "ZTFace251 [respcd=" + respcd + ", respinfo=" + respinfo
				+ ", mpssim=" + mpssim + ", user_check_result="
//				+ user_check_result + ", verify_result=" + verify_result
//				+ ", verify_similarity=" + verify_similarity
				+ ", image=" + image + "]";
	}
	
}
