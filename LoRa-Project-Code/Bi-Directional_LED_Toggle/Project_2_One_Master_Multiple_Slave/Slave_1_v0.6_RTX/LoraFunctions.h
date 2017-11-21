/*FUNCTIONS - with Self Descriptive Names
* String decodeReceivedString(String s_hex, String de_orig_val, String de_new_val)
* String charToHexString(char c)
* char hexStringToChar(String hexString)
* String stringToHexString(String s)
* String hexStringToString(String s_hex)
* String intToHexString(int i)
* int hexStringToInt(String s_hex)
* String returnHeaderString(String headerPlusDataHEXString)
* String returnMasterNumberAsString(String headerPlusDataHEXString)
* String returnNodeNumberAsString(String headerPlusDataHEXString)
* String returnDataString(String headerPlusDataHEXString)
* String returnHEXDataString(String headerPlusDataHEXString)
* int returnValue(String dataHexString, int whichValueMax20, String twoCharLongHEXDeLimiter)
*/

int hexStringToInt(String);

String decodeReceivedString(String s_hex, String de_orig_val, String de_new_val){
  String d_str="", p_str="", t_str="", a_str="", s_str="";
  int len, i=0;
  //s1="EA3A1B43A0" 

  s_hex.toUpperCase();
  len=s_hex.length();
  
  while(i<len){
    t_str=s_hex.substring(i,i+2);
    if(t_str!=de_orig_val){
      a_str += s_hex.substring(i,i+1); 
      d_str += s_hex.substring(i,i+1);
    }
    else{
       s_str += String(hexStringToInt(a_str))+de_new_val;
       a_str="";
       d_str+= de_new_val;
       i+=1;
    }    
    i++;
  }
  return s_str;
}

String charToHexString(char c){
  return String(c,HEX);
}

char hexStringToChar(String hexString){
  int i0,i1;
  
  i0 = (int)(hexString[0]);
  i1 = (int)(hexString[1]);
  
  if((i0>=65)&&(i0<=70)) i0 -= 55;
  else if((i0>=97)&&(i0<=102)) i0 -= 87;
  else i0 -= 48;
  
  if((i1>=65)&&(i1<=70)) i1 -= 55;
  else if((i1>=97)&&(i1<=102)) i1 -= 87;
  else i1 -= 48;
  
  return (char)(16*i0+i1);
}

String stringToHexString(String s){
  int i;
  String s_hex;
  for(i=0;i<s.length();i++){
    s_hex += charToHexString(s[i]);
  }
  return s_hex;
}

String hexStringToString(String s_hex){
  int i;
  String part_s_hex, s;
  for(i=0;i<s_hex.length();i+=2){
    part_s_hex = s_hex.substring(i,i+2);
    s += hexStringToChar(part_s_hex);
  }
  return s;
}

String intToHexString(int i){
  return String(i,HEX);
}

int hexStringToInt(String s_hex){
  int len, i, j, k, intOfS=0, powOf16;
  len = s_hex.length();
  for(i=0;i<len;i++){
    j=(int)(s_hex[len-1-i]);    
    if((j>=65)&&(j<=70)) j -= 55;
    else if((j>=97)&&(j<=102)) j -= 87;
    else j -= 48;
    powOf16=1;
    for(k=0;k<i;k++){
      powOf16 *= 16;
    }
    intOfS += j*powOf16;
  }
  return intOfS;
}

String returnMasterNumberAsString(String headerPlusDataHEXString){
  int iOf1;
  String headerPlusDataString = hexStringToString(headerPlusDataHEXString);
  iOf1 = headerPlusDataString.indexOf(":");
  String masterNumber = headerPlusDataString.substring(0,iOf1);
  return masterNumber;
}

String returnNodeNumberAsString(String headerPlusDataHEXString){
  int iOf1, iOf2;
  String headerPlusDataString = hexStringToString(headerPlusDataHEXString);
  iOf1 = headerPlusDataString.indexOf(":");
  iOf2 = headerPlusDataString.indexOf(":",iOf1+1);
  String nodeNumber = headerPlusDataString.substring(iOf1+1,iOf2);
  return nodeNumber ;
}

String returnHeaderString(String headerPlusDataHEXString){
  int iOf1, iOf2;
  String headerPlusDataString = hexStringToString(headerPlusDataHEXString);
  iOf1 = headerPlusDataString.indexOf(":");
  iOf2 = headerPlusDataString.indexOf(":",iOf1+1);
  String masterNumber = headerPlusDataString.substring(0,iOf1);
  String nodeNumber = headerPlusDataString.substring(iOf1+1,iOf2);  
  return masterNumber+":"+nodeNumber+":";
}

String returnDataString(String headerPlusDataHEXString){
  int iOf1h, iOf2h;
  iOf1h = headerPlusDataHEXString.indexOf("3A");
  iOf2h = headerPlusDataHEXString.indexOf("3A",iOf1h+2);
  String dataHexString = headerPlusDataHEXString.substring(iOf2h+2,headerPlusDataHEXString.length());
  return decodeReceivedString(dataHexString, "3A", ":");
}

String returnHEXDataString(String headerPlusDataHEXString){
  int iOf1h, iOf2h;
  iOf1h = headerPlusDataHEXString.indexOf("3A");
  iOf2h = headerPlusDataHEXString.indexOf("3A",iOf1h+2);
  String dataHexString = headerPlusDataHEXString.substring(iOf2h+2,headerPlusDataHEXString.length());
  return dataHexString;
}

int returnValue(String dataHexString, int whichValueMax20, String twoCharLongHEXDeLimiter){
  String t_str="", a_str="";
  int len, i=0, theValues[20], index=0;
  //s1="EA3A1B43A0" 

  len=dataHexString.length();
  
  while(i<len){
    t_str=dataHexString.substring(i,i+2);
    if(t_str!=twoCharLongHEXDeLimiter){
      a_str += dataHexString.substring(i,i+1); 
    }
    else{
       theValues[index++] = hexStringToInt(a_str);
       a_str="";
       i+=1;
    }    
    i++;
  }
  return theValues[whichValueMax20-1];
}
