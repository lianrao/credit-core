package com.wanda.credit.ds.client.policeAuthV2.beans;

/**
 * Created by on 2018/3/30.
 * 保留数据
 */
public class ReservedDataEntity {

    /**
     * sFXX : {"xM":"姓名","gMSFZHM":"公 民身份证号码 ","yXQJZRQ":"","yXQQSRQ":"","dN":"","mZDM":"","qFJG":"","xBDM":"","cSRQ":"","zZ":""}
     * wZXX : {"businessType":"门户网站 ","dealDate":"20171009","venderName":"网站名称 ","vendorIp":"www.chnctid.cn"}
     * zP : {"wLTZP":""}
     */

    private SFXXBean sFXX;
    private WZXXBean wZXX;
    private ZPBean zP;

    public SFXXBean getsFXX() {
        return sFXX;
    }

    public void setsFXX(SFXXBean sFXX) {
        this.sFXX = sFXX;
    }

    public WZXXBean getwZXX() {
        return wZXX;
    }

    public void setwZXX(WZXXBean wZXX) {
        this.wZXX = wZXX;
    }

    public ZPBean getzP() {
        return zP;
    }

    public void setzP(ZPBean zP) {
        this.zP = zP;
    }

    public static class SFXXBean {
        /**
         * xM : 姓名
         * gMSFZHM : 公 民身份证号码
         * yXQJZRQ :
         * yXQQSRQ :
         * dN :
         * mZDM :
         * qFJG :
         * xBDM :
         * cSRQ :
         * zZ :
         */

        private String xM;
        private String gMSFZHM;
        private String yXQJZRQ;
        private String yXQQSRQ;
        private String dN;
        private String mZDM;
        private String qFJG;
        private String xBDM;
        private String cSRQ;
        private String zZ;

        public String getxM() {
            return xM;
        }

        public void setxM(String xM) {
            this.xM = xM;
        }

        public String getgMSFZHM() {
            return gMSFZHM;
        }

        public void setgMSFZHM(String gMSFZHM) {
            this.gMSFZHM = gMSFZHM;
        }

        public String getyXQJZRQ() {
            return yXQJZRQ;
        }

        public void setyXQJZRQ(String yXQJZRQ) {
            this.yXQJZRQ = yXQJZRQ;
        }

        public String getyXQQSRQ() {
            return yXQQSRQ;
        }

        public void setyXQQSRQ(String yXQQSRQ) {
            this.yXQQSRQ = yXQQSRQ;
        }

        public String getdN() {
            return dN;
        }

        public void setdN(String dN) {
            this.dN = dN;
        }

        public String getmZDM() {
            return mZDM;
        }

        public void setmZDM(String mZDM) {
            this.mZDM = mZDM;
        }

        public String getqFJG() {
            return qFJG;
        }

        public void setqFJG(String qFJG) {
            this.qFJG = qFJG;
        }

        public String getxBDM() {
            return xBDM;
        }

        public void setxBDM(String xBDM) {
            this.xBDM = xBDM;
        }

        public String getcSRQ() {
            return cSRQ;
        }

        public void setcSRQ(String cSRQ) {
            this.cSRQ = cSRQ;
        }

        public String getzZ() {
            return zZ;
        }

        public void setzZ(String zZ) {
            this.zZ = zZ;
        }
    }

    public static class WZXXBean {
        /**
         * businessType : 门户网站
         * dealDate : 20171009
         * venderName : 网站名称
         * vendorIp : www.chnctid.cn
         */

        private String businessType;
        private String dealDate;
        private String venderName;
        private String vendorIp;

        public String getBusinessType() {
            return businessType;
        }

        public void setBusinessType(String businessType) {
            this.businessType = businessType;
        }

        public String getDealDate() {
            return dealDate;
        }

        public void setDealDate(String dealDate) {
            this.dealDate = dealDate;
        }

        public String getVenderName() {
            return venderName;
        }

        public void setVenderName(String venderName) {
            this.venderName = venderName;
        }

        public String getVendorIp() {
            return vendorIp;
        }

        public void setVendorIp(String vendorIp) {
            this.vendorIp = vendorIp;
        }
    }

    public static class ZPBean {
        /**
         * wLTZP :
         */

        private String wLTZP;

        public String getwLTZP() {
            return wLTZP;
        }

        public void setwLTZP(String wLTZP) {
            this.wLTZP = wLTZP;
        }
    }
}