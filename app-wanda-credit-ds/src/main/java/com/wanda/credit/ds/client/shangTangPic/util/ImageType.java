package com.wanda.credit.ds.client.shangTangPic.util;

public enum ImageType {
	JPG,JPEG,BMP,PNG,GIF,TIFF;
	
	public static boolean contains(String type){    
        for(ImageType typeEnum : ImageType.values()){    
            if(typeEnum.name().equals(type)){    
                return true;    
            }    
        }    
        return false;    
    }  
}
