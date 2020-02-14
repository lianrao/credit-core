package com.wanda.credit.ds.client.policeAuthV2.beans;

/**
 * Created by on 2018/3/29.
 */
public class AuthenticationData {
    /**
     * bizPackage : {"customNumber":"","appName":"","timeStamp":"","businessSerialNumber":"","authMode":"","photoData":"","authCode":"","idcardAuthData":"","authApplyRetainData":""}
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
         * customNumber :
         * appName :
         * timeStamp :
         * businessSerialNumber :
         * authMode :
         * photoData :
         * authCode :
         * idcardAuthData :
         * authApplyRetainData :
         */

        private String customNumber;
        private String appName;
        private String timeStamp;
        private String businessSerialNumber;
        private String authMode;
        private String photoData;
        private String authCode;
        private String idcardAuthData;
        private String authApplyRetainData;

        public String getCustomNumber() {
            return customNumber;
        }

        public void setCustomNumber(String customNumber) {
            this.customNumber = customNumber;
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

        public String getBusinessSerialNumber() {
            return businessSerialNumber;
        }

        public void setBusinessSerialNumber(String businessSerialNumber) {
            this.businessSerialNumber = businessSerialNumber;
        }

        public String getAuthMode() {
            return authMode;
        }

        public void setAuthMode(String authMode) {
            this.authMode = authMode;
        }

        public String getPhotoData() {
            return photoData;
        }

        public void setPhotoData(String photoData) {
            this.photoData = photoData;
        }

        public String getAuthCode() {
            return authCode;
        }

        public void setAuthCode(String authCode) {
            this.authCode = authCode;
        }

        public String getIdcardAuthData() {
            return idcardAuthData;
        }

        public void setIdcardAuthData(String idcardAuthData) {
            this.idcardAuthData = idcardAuthData;
        }

        public String getAuthApplyRetainData() {
            return authApplyRetainData;
        }

        public void setAuthApplyRetainData(String authApplyRetainData) {
            this.authApplyRetainData = authApplyRetainData;
        }

        @Override
        public String toString() {
            return "BizPackageBean{" +
                    "customNumber='" + customNumber + '\'' +
                    ", appName='" + appName + '\'' +
                    ", timeStamp='" + timeStamp + '\'' +
                    ", businessSerialNumber='" + businessSerialNumber + '\'' +
                    ", authMode='" + authMode + '\'' +
                    ", photoData='" + photoData + '\'' +
                    ", authCode='" + authCode + '\'' +
                    ", idcardAuthData='" + idcardAuthData + '\'' +
                    ", authApplyRetainData='" + authApplyRetainData + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AuthenticationData{" +
                "bizPackage=" + bizPackage +
                ", sign='" + sign + '\'' +
                '}';
    }
}
