package com.wanda.credit.ds.client.policeAuthV2.beans;

/**
 * Created by on 2018/3/26.
 */
public class AuthenticationApplicationDataCert {
 
    private BizPackageBean bizPackage;
    private String sign;

    public BizPackageBean getBizPackage() {
        return bizPackage;
    }

    public void setBizPackage(BizPackageBean bizPackage) {
        this.bizPackage = bizPackage;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public static class BizPackageBean {
        /**
         * customerNumber :
         * appName :
         * timeStamp :
         * cardReaderVersion :
         * liveDetectionControlVersion :
         * authCodeControlVersion :
         * authMode :
         */

        private String custNum;
        private String appName;
        private String timeStamp;
        private String bizSerialNum;
        private String liveDetectCtrlVer;
        private String idAuthData;
        private int authMode;



        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
        }

		public String getCustNum() {
			return custNum;
		}

		public void setCustNum(String custNum) {
			this.custNum = custNum;
		}

		public String getBizSerialNum() {
			return bizSerialNum;
		}

		public void setBizSerialNum(String bizSerialNum) {
			this.bizSerialNum = bizSerialNum;
		}

		public String getIdAuthData() {
			return idAuthData;
		}

		public void setIdAuthData(String idAuthData) {
			this.idAuthData = idAuthData;
		}

		public int getAuthMode() {
			return authMode;
		}

		public void setAuthMode(int authMode) {
			this.authMode = authMode;
		}

		public String getLiveDetectCtrlVer() {
			return liveDetectCtrlVer;
		}

		public void setLiveDetectCtrlVer(String liveDetectCtrlVer) {
			this.liveDetectCtrlVer = liveDetectCtrlVer;
		}

        
    }
}
