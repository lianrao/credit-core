package com.wanda.credit.ds.client.policeAuthV2.beans;

/**
 * Created by on 2018/3/26.
 */
public class AuthenticationApplicationData {
    /**
     * bizPackage : {"customerNumber":"","appName":"","timeStamp":"","cardReaderVersion":"","liveDetectionControlVersion":"","authCodeControlVersion":"","authMode":""}
     * sign :
     */

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

        private String customerNumber;
        private String appName;
        private String timeStamp;
        private String cardReaderVersion;
        private String liveDetectionControlVersion;
        private String authCodeControlVersion;
        private String authMode;

        public String getCustomerNumber() {
            return customerNumber;
        }

        public void setCustomerNumber(String customerNumber) {
            this.customerNumber = customerNumber;
        }

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

        public String getCardReaderVersion() {
            return cardReaderVersion;
        }

        public void setCardReaderVersion(String cardReaderVersion) {
            this.cardReaderVersion = cardReaderVersion;
        }

        public String getLiveDetectionControlVersion() {
            return liveDetectionControlVersion;
        }

        public void setLiveDetectionControlVersion(String liveDetectionControlVersion) {
            this.liveDetectionControlVersion = liveDetectionControlVersion;
        }

        public String getAuthCodeControlVersion() {
            return authCodeControlVersion;
        }

        public void setAuthCodeControlVersion(String authCodeControlVersion) {
            this.authCodeControlVersion = authCodeControlVersion;
        }

        public String getAuthMode() {
            return authMode;
        }

        public void setAuthMode(String authMode) {
            this.authMode = authMode;
        }
    }
}
