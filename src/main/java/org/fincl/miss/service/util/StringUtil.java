/*
 * @Project Name : miss-biz
 * @Package Name : org.fincl.miss.service.util
 * @파일명          : StringUtil.java
 * @작성일          : 2015. 8. 20.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 20.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.service.util;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
/**
 * @파일명          : StringUtil.java
 * @작성일          : 2015. 8. 20.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 20.   |   ymshin   |  최초작성
 */
public class StringUtil {
	 /** 영문 + 숫자 */
    final static String EN
        = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    /** 숫자 */
    final static String NUM = "0123456789";
    
    final static String INVAILD_CHAR = "+_-=|/><:;\\";
    /** 생성자 */
    public StringUtil() {}
    /**
    * 함수명 :  통신 전문 처리
    * @FuncDesc : 통신 전문을 파싱한다.
    *
    * @param data string to be splitted
    * @param rule splitting rule
    * @return splitted string array
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String[] split(String data, int[] rule) {
        String parsed[] = new String[rule.length];
        byte[] bytes = data.getBytes();
        int byteslen = Array.getLength(bytes);
        int pointer = 0;
        for (int i = 0; i < rule.length; i++) {
            if(byteslen < pointer) {
                parsed[i] = new String("");
            } else {
                parsed[i] = new String(bytes, pointer, rule[i]);
            }
            pointer += rule[i];
        }
        return parsed;
    }
    

    /**
    * 함수명 :  통신 전문 처리
    * @FuncDesc : 통신 전문을 파싱한다.
    *
    * @param data string to be splitted
    * @param rule splitting rule
    * @return splitted string array
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String[] split_string(String data, int[] rule){
        String parsed[] = new String[rule.length];
        int off = 0;
        for(int i=0; i<rule.length; i++){
            parsed[i] = data.substring(off, off+rule[i]);  off += rule[i];
        }
        return parsed;
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 주민번호 사이에 '-'를 넣는다.
    *
    * @param amountString raw string
    * @return formatted string
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String formatJumin(String juminString) {
        if(juminString == null) return "";
        if(juminString.length() != 13) return juminString;
        return juminString.substring(0,6)+"-"+juminString.substring(6);
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 숫자에 세자리마다 쉼표를 찍는다.
    *
    * @param amountString raw string
    * @return formatted string
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String formatCurrency(String amountString) {
        try {
            double amount = Double.parseDouble(amountString);
            return formatCurrency(amount);
        } catch (NumberFormatException ex) {
            return amountString;
        }
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 숫자에 세자리마다 쉼표를 찍는다.
    *
    * @param amount raw int number
    * @return formatted string
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String formatCurrency(int amountInt) {
        double amount = new Integer(amountInt).doubleValue();
        return formatCurrency(amount);
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 숫자에 세자리마다 쉼표를 찍는다.
    *
    * @param amount raw double number
    * @return formatted string
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String formatCurrency(double amount) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        DecimalFormat df = (DecimalFormat)nf;
        String pattern = "###,###,###,###";
        df.applyPattern(pattern);
        return df.format(amount);
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : rate를 나타내는 format.
    *
    * @param pRate raw string
    * @return formatted string
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String formatRate(String pRate) {
        return formatRate(pRate, 2);
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 날짜 사이에 '/'를 넣는다.
    *
    * @param amountString raw string
    * @return formatted string
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String formatdate(String wdate) {
        if(wdate == null) return "";
        if(wdate.length() < 8) return wdate;
        return wdate.substring(0,4)+"."+wdate.substring(4,6)+"."
            +wdate.substring(6,8);
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 날짜 사이에 구분자를 넣는다.
    *
    * @param wdate 날짜(yyyymmdd)
    * @param gubun 구분자
    * @return formatted string
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String formatdate(String wdate, String gubun) {
        if(wdate == null) return "";
        if(wdate.length() < 8) return wdate;
        return wdate.substring(0,4)+gubun+wdate.substring(4,6)
            +gubun+wdate.substring(6,8);
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 계좌번호 사이에 '-'를 넣는다.(000-000000-00000)
    *
    * @param amountString raw string
    * @return formatted string
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String formatBankNo(String bankNoString) {
        if(bankNoString == null) return "";
        if(bankNoString.length() != 14) return bankNoString;
        return bankNoString.substring(0,3)+"-"+bankNoString.substring(3,9)
            +"-"+bankNoString.substring(9,14);
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 프리아이에서 카드 종류를 구별한다.
    *
    * @param cardType 카드구분
    * @return cardType
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String formatCardType(String cardType){
        if(cardType==null)return "";
        if(cardType.indexOf("프리아이")!=-1) {
            return cardType.substring(0,cardType.indexOf("프리아이"))
                + cardType.substring((cardType.indexOf("프리아이")+4),
                cardType.length());
        } else  {
            return cardType;
        }
    }

    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : rate를 나타내는 format.
    *
    * @param pRate raw string
    * @param pPost raw count
    * @return formatted string
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String formatRate(String pRate, int pPost) {
        String vReturn = null;
        if(pRate == null) return "";
        if(pPost <= 0) return pRate;
        if(pRate.length() <= pPost) return pRate;

      int vIndex = pRate.length() - pPost;
        vReturn = formatCurrency(pRate.substring(0,vIndex));
        vReturn = vReturn + "." + pRate.substring(vIndex);

        return vReturn;
    }

    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 보안을 위해 문자열의 일부를 *로 바꾼다.
    * <pre>
    * The asterisk(*) begins at the specified beginIndex
    * and extends to the character at index endIndex - 1.
    * Thus the length of the *s' is endIndex-beginIndex.
    * </pre>
    * @param beginIndex the beginning index, inclusive.
    * @param endIndex the ending index, exclusive.
    * @return partially replaced string by asterisk.
    * @exception IndexOutOfBoundsException if the beginIndex is negative,
    * or endIndex is larger than the length of this String object,
    * or beginIndex is larger than endIndex.
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String hide(String exposed, int beginIndex, int endIndex) {
        if(exposed == null) return "";
        if(exposed.length() < beginIndex) beginIndex = exposed.length();
        if(exposed.length() < endIndex)   endIndex   = exposed.length();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < exposed.length(); i++) {
            if ( i >= beginIndex && i < endIndex)
                buffer.append("*");
            else
                buffer.append(exposed.charAt(i));
        }
        return buffer.toString();
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 보안을 위해 문자열의 일부를 *로 바꾼다.
    * <pre>
    * The asterisk(*) begins at the specified beginIndex
    * and extends to the character at last index.
    * Thus the length of the *s' is string length - beginIndex.
    * </pre>
    * @param beginIndex the beginning index, inclusive.
    * @return partially replaced string by asterisk.
    * @exception IndexOutOfBoundsException if the beginIndex is negative,
    * or beginIndex is larger than string length.
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String hide(String exposed, int beginIndex) {
        if(exposed == null) return "";
        return hide(exposed,beginIndex,exposed.length());
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 보안을 위해 문자열의 일부를 *로 바꾼다.
    * <pre>
    * The asterisk(*) begins at the specified length - rearIndex
    * and extends to the character at last index.
    * Thus the length of the *s' is string rearIndex.
    * </pre>
    * @param beginIndex the beginning index, inclusive.
    * @return partially replaced string by asterisk.
    * @exception IndexOutOfBoundsException if the beginIndex is negative,
    * or beginIndex is larger than string length.
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String hideRear(String exposed, int rearIndex) {
        if(exposed == null) exposed = "";
        exposed.trim();
        if(exposed.length() < rearIndex) rearIndex = exposed.length();
        return hide(exposed, exposed.length() - rearIndex, exposed.length());
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 카드번호 사이에 '-', '*' 를 넣는다.(0000-****-****-000*)
    *
    * @param amountString raw string
    * @return formatted card number
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String formatCardDecrypt(String cardNoString) {
        if(cardNoString == null) return "";
        if(cardNoString.length() != 16) return cardNoString;
        return cardNoString.substring(0,4)+" - **** - **** - "
            +cardNoString.substring(12,15)+"*";
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 계좌번호 뒤 4자리를 '*'처리함.
    *
    * @param amountString raw string
    * @return formatted account
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String hideAccount(String account) {
        return hideRear(account,4);
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 계좌번호를 '*'처리함
    *
    * @param amountString raw string
    * @param cnt '*'처리할 계좌번호의 수
    * @return formatted account
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String hideAccount(String account, int cnt) {
        return hideRear(account, cnt);
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 주민번호 뒤 7자리 '*'처리
    *
    * @param ssn 주민번호(13)
    * @return formatted ssn
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String hideSSN(String ssn) {
        return hideRear(ssn,7);
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 카드번호 가운자 8자리 '*'처리
    *
    * @param card 카드번호(16)
    * @return formatted card number
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String hideCardNo(String card) {
        if(card == null) return "";

        //TODO 문제 있음 (아맥스 관련)
        if( card.length()==16 ) {
            card = card.substring(0,4)+"********"+card.substring(12,15)+"*";
        }else if( card.length()==15 ) {
            card = card.substring(0,4)+"********"+card.substring(12,14)+"*";
        }else{
            return "";
        }
        return card;
        //return hide(card.trim(), 4, 12);
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 카드번호 사이에 '-'를 넣는다.(0000-0000-0000-0000)
    *
    * @param amountString raw string
    * @return formatted string
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String formatCardNo(String cardNoString) {
        if(cardNoString == null) return "";
        if(cardNoString.length() != 16) return cardNoString;
        return cardNoString.substring(0,4)+"-"+cardNoString.substring(4,8)
            +"-"+cardNoString.substring(8,12)+"-"+cardNoString.substring(12,16);
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 특정길이의 스페이스를 리턴한다.
    *
    * @param len 전체 스페이스의 길이
    * @return spaces
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String space(int len) {
        StringBuffer buffer = new StringBuffer(len);
        for (int i = 0; i < len; i++) {
            buffer.append(" ");
        }
        return buffer.toString();
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc :  배열의 길이를 모두 더한 크기의 스페이스를 만든다.
    *
    * @param rule 길이 배열
    * @return spaces
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String space(int[] rule) {
        return space(sum(rule));
    }
    
    /**
     * int array의 int값을 모두 더한다.
     *
     * @return total sum
     * @param rule int array
     */
    public static int sum(int[] rule) {
        int len = 0;
        for (int i = 0; i < rule.length; i++) {
            len += rule[i];
        }
        return len;
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc :  주어진 개수만큼 스페이스를 버퍼에 쓴다.
    *
    * @param len 스페이스의 개수
    * @return spaces
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static void writeSpace(StringBuffer buffer, int len) {
        for (int i = 0; i < len; i++) {
            buffer.append(" ");
        }
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc :  주어진 개수만큼 zero를 버퍼에 쓴다.
    *
    * @param len zero의 길이
    * @return zeros
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static void writeZero(StringBuffer buffer, int len) {
        for (int i = 0; i < len; i++) {
            buffer.append("0");
        }
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc :  버퍼에 zero를 쓰고 주어진 문자를 마지막에 붙인다.
    *
    * @param subject 맨 뒤에 붙일 문자
    * @param len 버퍼에 쓰여지는 문자의 개수
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static void writeLeadingZero(StringBuffer buffer,
        String subject, int len) {
        writeZero(buffer, len - subject.length());
        buffer.append(subject);
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc :  버퍼에 주어진 문자를 쓰고 마지막에 space를 붙인다.
    *
    * @param subject 맨 앞에 붙일 문자
    * @param len 버퍼에 쓰여지는 문자의 개수
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static void writeTailedSpace(StringBuffer buffer, String subject,
        int len) {
        buffer.append(subject);
        writeSpace(buffer, len - subject.length());
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc :  주어진 Character를 추가하는 메소드(뒷자리 부터 추가)
    *
    * @param data 데이터
    * @param len  format length
    * @param addChar 추가할 char
    * @return formatted String
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String addCharB( String data, int len, String addChar ) {
        if( data==null ) data="";
        if( addChar==null ) addChar="";
        byte b[]=data.getBytes();
        int mlen=b.length;
        int j=len-mlen;
        for(int i=0;i<j ;i++) data=data + addChar;
        return data;
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc :  주어진 Character를 추가하는 메소드(앞자리 부터 추가)
    *
    * @param data 데이터
    * @param len  format length
    * @param addChar 추가할 char
    * @return formatted String
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String addCharF( String data, int len, String addChar ) {
        if( data==null ) data="";
        if( addChar==null ) addChar="";
        byte b[]=data.getBytes();
        int mlen=b.length;
        int j=len-mlen;
        for(int i=0;i<j ;i++) data = addChar + data;
        return data;
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc :  데이터가 영문자와 숫자만으로 이루어져 있는지 체크한다.
    *
    * @param data 데이터
    * @return boolean
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static boolean isAlphaNumeric( String data) {
        if( data==null || data.equals("") ) return false;
        for (int i=0; i<data.length(); i++) {
            String ch = data.substring(i, i+1);
            if ( (EN + NUM).indexOf( ch ) == -1 ) return false;
        }
        return true;
    }
    
    /**
     * 함수명 :  데이터 처리
     * @FuncDesc :  데이터가 패스워드 제외문자가 포함하는지 체크한다.
     *
     * @param data 데이터
     * @return boolean
     * @Author     : 
     * @history 수정일자 / 수정자 / 수정내용
     * 2004.10.06 /  / 최초작성
     */
     public static boolean isPasswordVaild( String data) {
         if( data==null || data.equals("") ) return false;
         for (int i=0; i<data.length(); i++) {
             String ch = data.substring(i, i+1);
             if ( (INVAILD_CHAR).indexOf( ch ) != -1 ) return false;
         }
         return true;
     }
     
     public static String inVaildString(){
    	 return INVAILD_CHAR;
     }
     
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc :  인수로 받은 데이터가 영문자만으로 이루어져 있는지 체크
    *
    * @param data 데이터
    * @return boolean
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static boolean isEnglish( String data) {
        if( data==null || data.equals("") ) return false;
        for (int i=0; i<data.length(); i++) {
            String ch = data.substring(i, i+1);
            if ( EN.indexOf( ch ) == -1 ) return false;
        }
        return true;
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 인수로 받은 데이터가 숫자만으로 이루어져 있는지 체크한다.
    *
    * @param data 데이터
    * @return boolean
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static boolean isNumeric( String data) {
        if( data==null || data.equals("") ) return false;
        for (int i=0; i<data.length(); i++) {
            String ch = data.substring(i, i+1);
            if ( NUM.indexOf( ch ) == -1 ) return false;
        }
        return true;
    }
    
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 주어진 값이 일치할 경우 'selected'를 리턴한다.
    *
    * @param valueA 비교값
    * @param valueB 비교값
    * @return String
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String isSelected( String valueA, String valueB ) {
        return valueA.equals(valueB)?"selected":"";
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 주어진 값이 일치할 경우 'checked'를 리턴한다.
    *
    * @param valueA 비교값
    * @param valueB 비교값
    * @return String
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String isChecked( String valueA, String valueB ) {
        return valueA.equals(valueB)?"checked":"";
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 구분에 따른 테스트 필드 적용 gubunA와 gubunB가
    *  같은 경우 textA 값 리턴
    * @param gubunA parameter로 받은 값
    * @param gubunB user 입력 값
    * @param textA 텍스트A
    * @param textB 텍스트B
    * @return String
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String setText( String gubunA, String gubunB, String textA,
         String textB) {
        return gubunA.equals(gubunB) ? textA : textB;
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : typeA(B)에 따라 주어진 길이(len)의  데이터(data)를 재생성한다
    * @param data 데이터
    * @param len 길이
    * @param typeA 채울 캐릭터(S이면 Space, N이면 Number)
    * @param typeB Align( L이면 Left, R이면 Right)
    * @return String
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String addChar( String data, int len, String typeA,
        String typeB ) throws Exception{
        byte b[]=data.getBytes();
        String addchar = "0";
        if( typeA.equals("S") ) {
            addchar = " ";
        }
        if( typeB.equals("L") ) {
            for(int i=0;i< (len-b.length) ;i++){
                data += addchar;
            }
        }else{
            for(int i=0;i< (len-b.length) ;i++){
                   data = addchar + data;
            }
        }
        //한글일 경우(2byte)를 대비한 코드
        if( b.length > len ) {
            data = Two_bytes( data, len);
        }
        return data;
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : typeA(B)에 따라 주어진 길이(len)의  데이터(data)를 재생성
    * @param data 데이터
    * @param len 길이
    * @param typeA 채울 캐릭터(S이면 Space, N이면 Number)
    * @param typeB Align( L이면 Left, R이면 Right)
    * @return String
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String setChar( String data, int len, String typeA, String typeB ) throws Exception{
        byte b[]=data.getBytes();
        String addchar = "0";
        if( typeA.equals("S") ) {
            addchar = " ";
        }
        if( typeB.equals("L") ) {
            for(int i=0;i< (len-b.length) ;i++){
                data += addchar;
            }
        }else{
            for(int i=0;i< (len-b.length) ;i++){
                   data = addchar + data;
            }
        }
        //숫자 체크
        if( typeA.equals("N") ) {
            if( !isNumeric( data ) ) {
                throw new Exception("DATA FORMAT ERROR!");
            }
        }
        //한글일 경우(2byte)를 대비한 코드
        if( b.length > len ) {
            throw new Exception("입력길이 초과 : [" + data + "]");
        }
        return data;
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 주어진 단어를 two byte형태로 바꾼후 리턴
    * @param sorc 바꿀 단어
    * @param di 정해진 길이
    * @return String 2byte의 문자리턴
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String Two_bytes(String sorc, int di) {
        int     i=0, j=0;
        if(di < 0) return "";	//2019.02.14
        
        byte[] b1 =new byte[di];
        byte[] b2=sorc.getBytes();
        for(i=0, j=0; i<b2.length && j<di ; i++, j++) {
            if (b2[i] == ' ') {
                b1[j++] = (byte)0xa1;
                b1[j] =  (byte)0xa1;
            }else if ((b2[i] & (byte)0x80) < 0) {
                b1[j++] = b2[i++];
                b1[j] = b2[i];
            }else {
                b1[j++] =  (byte)0xa3;
                b1[j] =  (byte)(b2[i] +  (byte)0x80);
            }
        }
        return new String(b1);
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : null일 경우 ""로 리턴
    * @param str : 처리할 문자열
    * @return string
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String nullToBlank(String str)    {
        if(str == null) {
            return "";
        }else{
            return str;
        }
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 한글 코드 변환
    * @param s ksc5601 코드변환 처리할 문자열
    * @return string
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String toKorean(String s) throws Exception    {
        if(s != null) {
            return (new String(s.getBytes("8859_1"), "ksc5601"));
        }else{
            return s;
        }
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 한글 코드 변환
    * @param s ksc5601 코드변환 처리할 문자열
    * @return string[]
    * @Author     :
    * @history 수정일자 / 수정자 / 수정내용
    */
    public static String[] toKorean(String[] s) throws Exception    {
        String[] v= s;
        int i=0;
        if(s != null) {
            if(s.length>0){
                for(i=0;i<s.length; i++){
                    v[i] = (new String(s[i].getBytes("8859_1"), "ksc5601"));
                }
            }
            return v;
        }else{
            return s;
        }
    }

    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 한글 코드 변환
    * @param s : 8859_1 코드변환 처리할 문자열
    * @return string
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String toEnglish(String s) throws Exception {
        if(s != null) {
            return (new String(s.getBytes("ksc5601"), "8859_1"));
        }else{
            return s;
        }
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 문자열 치환
    * @param str : 처리할 문자열
    * @param pattern : 처리할 문자열 중 찾을 문자열
    * @param replace : 바꿀 문자열
    * @return string
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String replace(String str, String pattern, String replace) {
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();

        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e + pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : Space를 뒤에 추가
    * @param 처리할 String
    * @return formatted string
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String addSpace(String data, int len) throws Exception  {
        if( data==null ) data = "";
        if( data.length() > len ) throw new Exception("Data Length Error");

        int mlen = len - data.length();
        StringBuffer sb = new StringBuffer();
        sb.append(data);
        for(int i=0;i<mlen;i++) sb.append(" ");
        return sb.toString();
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : Space를 뒤에 추가 하는 메소드 - 한글은 2Byte로 처리됨
    * @param 처리할 String
    * @return formatted string
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String addSpaceByte(String str, int len) throws Exception  {
        if( str==null ) str = "";
        if( str.length() > len ) throw new Exception("Data Length Error");

        int slen=0, rlen=0, mlen=0;
        StringBuffer sb = new StringBuffer();
        if(str == null) {
            str = "";
        }
                String tmp = str;
        char c;
        sb.append(str);

        slen = str.length();

        for(int i=0; i<slen;i++) {
            c = tmp.charAt(i);
            rlen++;
            if ( c  > 127 ) rlen++;  //2-byte character..
        }
        mlen = len - rlen;
        for(int i=0;i<mlen;i++) sb.append(" ");

        return sb.toString();
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : "0"를 앞에 추가
    * @param 처리할 String
    * @return formatted string
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String addZero(String data, int len) throws Exception  {
        if( data==null ) data = "";
        if( data.length() > len ) throw new Exception("Data Length Error");
        int mlen = len - data.length();
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<mlen;i++) sb.append("0");
        sb.append(data);
        return sb.toString();
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 숫자에 세자리마다 쉼표를 찍는다. 소수까지 처리
    * @param 처리할 String
    * @return formatted string
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String addComma(String src_str) {
        String result = "";
        boolean decimal_fg = false;
        DecimalFormat decimalformat = null;

        try {
            src_str = src_str.trim();

            for(int i=0; i<src_str.length(); i++) {
                if('.' == src_str.charAt(i)) {
                    decimal_fg = true;
                    break;
                }
            }

            if(decimal_fg) decimalformat
                = new DecimalFormat("###,###,###,###,##0.00");
            else    decimalformat = new DecimalFormat("###,###,###,###,###");

            result
                = decimalformat.format(Double.valueOf(src_str).doubleValue());
        } catch(Exception _ex) {
            result = src_str;
        }
        return result;
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 문자열을 Integer형으로 리턴(null일 경우,숫자가 아닐 경우 처리)
    * @param str : 바꿀 문자열
    * @param delim splitting delimeter
    * @return int : 바뀐 문자열
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static int toInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch(Exception e) {
            return 0;
        }
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 문자열의 길이가 길 경우 str 을 i 의 길이로 자른다.
    * @param Stirng str
    * @param int i
    * @return String
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String crop(String str, int i, String trail) {
        if (str==null) return "";
        String tmp = str;
        if(tmp.length()>i) tmp=tmp.substring(0,i)+trail;
        return tmp;
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 문자열의 길이가 길 경우 str 을 i 의 길이로 자른다.
    * <pre>
    * 문자열 바이트수만큼 끊어주고, 생략표시하기
    * 한글은 2byte 영문은 1byte로 계산
    * 한글이 자르는 바이트에 걸릴 경우 그 이전 문자까지만 리턴
    * </pre>
    * @param str 문자열
    * @param i 바이트수
    * @param trail 생략 문자열. 예) "..."
    * @return String
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String cropByte(String str, int CUT_LEN, String trail) {
        if (str==null) return "";
        String tmp = str;
        int slen = 0, blen = 0;
        char c;
        try {
            if(tmp.getBytes("MS949").length>CUT_LEN) {
                while (blen < CUT_LEN) {
                    c = tmp.charAt(slen);
                    blen++;
                    slen++;
                    if ( c  > 127 ){
                        blen++;  //2-byte character..
                        if(blen > CUT_LEN ){
                            slen--;
                        }
                    }
                }
                tmp=tmp.substring(0,slen)+trail;
            }
        } catch(java.io.UnsupportedEncodingException e) {}
        return tmp;
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 문자열을 시작지점부터 끝 지점까지 자른다.
    * <pre>
    * 문자열 바이트수만큼 끊어주고, 생략표시하기
    * 한글은 2byte 영문은 1byte로 계산
    * 한글이 자르는 바이트에 걸릴 경우 그 이전 문자까지만 리턴
    * 문자열의 시작은 0부터 시작한다.
    * </pre>
    * @param str 문자열
    * @param start 자르는 시작지점
    * @param end 자르는 끝지점
    * @return String
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2008.04.17 /  / 최초작성
    */
    public static String cutStringByte(String str, int start, int end) {
        if (str==null) return "";
        String resultData = "";
        try{
            byte [] tmp_byte = new byte[5120];
            byte [] origin_string = str.getBytes("MS949");
            int index = 0;
            for(int t = start; t < end; t++) {
                if(t-start < 0 ) {
                    index = 0;
                }else {
                    index = t-start;
                }
                tmp_byte[index] = origin_string[t];
            }

            resultData = new String(tmp_byte);
        }catch(Exception e) {
            System.out.println("Exception:"+e.getMessage());
        }
        return  resultData;
    }

    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 문자열 바이트수만큼 끊어서 나누어주기
    *  한글은 2byte 영문은 1byte로 계산
    * @param str 문자열
    * @param i 바이트수
    * @param trail 나누어줄 문자열
    * @return String
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String splitByte(String data, int CUT_LEN, String trail)    {
        String temp1 = data;
        String temp2=data, out_str="";
        int alen=0, blen=0;
        int data_len=0, temp2_len=0;
        char c;

        try {
            data_len = temp1.getBytes("MS949").length;
            if(data_len <= CUT_LEN)
                return data;

            while(true) {
                temp1 = temp2;
                alen=0; blen = 0;
                           while (blen < CUT_LEN) {
                    c = temp1.charAt(alen);
                    alen++;
                    blen++;
                    if ( c  > 127 ){
                        blen++;  //2-byte character..
                        if(blen > CUT_LEN ){
                            alen--;
                        }
                    }
                }

                temp1=temp1.substring(0,alen)+trail;
                out_str += temp1;
                temp2 = temp2.substring(alen);

                temp2_len = temp2.getBytes("MS949").length;

                if(temp2_len < CUT_LEN) {
                    out_str += temp2;
                    break;
                }
            }
            return out_str;

        } catch(Exception e) {
            System.out.println("Exception:"+e.getMessage());
            return data;
        }
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 주민번호 체크
    * @param jumin1 주민번호 앞 6자리,
    * @param jumin2 주민번호 뒤 7자리
    * @return boolean
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static boolean isValidJuminNo(String jumin1, String jumin2)    {
        if(!isNumeric(jumin1) || jumin1.length() != 6) return false;
        if(!isNumeric(jumin2) || jumin2.length() != 7) return false;

        String jumin_no = jumin1 + jumin2;
        int [] a = new int[13];

        for (int i=0; i<13; i++) {
            a[i] = Integer.parseInt(""+jumin_no.charAt(i));
        }

        int j=0, k=0;
        j = a[0]*2 + a[1]*3 + a[2]*4 + a[3]*5 + a[4]*6 + a[5]*7 + a[6]*8
            + a[7]*9 + a[8]*2 + a[9]*3 + a[10]*4 + a[11]*5;
        j = j % 11;
        k = 11 - j;
        if (k > 9) {
            k = k % 10;
        }
        if (k != a[12]) return false;
        return true;
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 배열에서 선택한 값을 표시-A배열의 위치에 있는 B배열의 값 리턴
    * @param arrName[]  : 선택 코드값들
    * @param arrValue[] : 코드에 해당하는 각각의 값들
    * @param selectedValue : 선택 코드
    * @return String
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2004.10.06 /  / 최초작성
    */
    public static String selectData(String[] arrName, String[] arrValue,
        String selectName) {
        if(arrName.length != arrValue.length)
            return "";
        for(int i=0; i < arrName.length; i++ ) {
            if ( selectName.equals(arrName[i]) ) {
                return arrValue[i];
            }
        }
        return "";
    }
    /**
    * 함수명 :  데이터 처리
    * @FuncDesc : 특수 문자 값을 없애준다.
    * @param str  : 넘어오는값
    * @return String
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2007.10.06 /  / 최초작성
    */
    public static String getNumberStringImp(String str) {
        StringBuffer numBuf = new StringBuffer(30);
        int len = str.length();
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (c >= '0' && c <= '9') {
                numBuf.append(c);
            }
        }
        return numBuf.toString();
    }


    /**
    * Empty여부를 확인한다.
    * @FuncDesc : Empty여부를 확인한다. (Empty란 null 혹은 "" 을 의미한다. )
    * @param str  : 넘어오는값
    * @return  boolean
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2008.04.17 /  / 최초작성
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }
    
    /**
    * Empty여부를 확인한다.
    * @FuncDesc : Empty여부를 확인한다. (Empty란 null 혹은 "" 을 의미한다. )
    * @param str  : 넘어오는값
    * @return  boolean
    * @Author     : 
    * @history 수정일자 / 수정자 / 수정내용
    * 2008.04.17 /  / 최초작성
     */
    public static boolean isEmpty(String[] str) {
        return str == null || str.length == 0;
    }

    /**
    * version 1.3에서 제공하지 않는 String.sprit를 대신 처리하는 메소드
    * @FuncDesc :
    * @param source
    * @param divide
    * @return String[]
    * @author 김정수
    * @history 수정일자 / 수정자 / 수정내용
    * 2008.04.22 /  / 최초작성
    */
    public static String[] StringSprit(String source, String divide){

        StringTokenizer tokens = new StringTokenizer( source,divide);
        ArrayList arraylist = new ArrayList();

        while (tokens.hasMoreElements()) {
            arraylist.add( tokens.nextElement());
        }
        return (String[])arraylist.toArray(new String[arraylist.size()]);


    }

    /**
    * @계좌번호 마스킹
    * @param account
    * @return
    */
    public static String getAccount(String account){
        StringBuffer sb = new StringBuffer();
        String result = "";
        account = account.trim();
        if(account == null) return "";

        if(!"".equals(account)){
            sb.append(account.substring(0, account.length()-6));
            sb.append("*****");
            sb.append(account.substring(account.length()-1, account.length()));
            result = sb.toString();
        }else{
            result = "";
        }
        return result;
    }

    /**
    * 날짜 format
    * @FuncDesc : 날짜 사이에 구분자를 넣는다.
    * @param wdate 날짜(yymmdd)
    * @param gubun 구분자
    */
    public static String getDateFormat(String date, String gubun) {
        if(date == null) return "";
        if(date.length() < 8) return date;
        return date.substring(2,4)+gubun+date.substring(4,6)
            +gubun+date.substring(6,8);
    }


    /**
    *
    * @param serviceCode
    * @return
    */
    public static String getNaveCode(String serviceCode ) {

        StringBuffer sb = new StringBuffer();
        if ( serviceCode.length() < 8 )  serviceCode="        ";
        if (isNumeric(  serviceCode.substring(2,4)  ))
             sb.append( Integer.parseInt(serviceCode.substring(2, 4)) );
        else  sb.append(1) ;

        sb.append(",");
        if (isNumeric(  serviceCode.substring(4,6)  ))
             sb.append( Integer.parseInt(serviceCode.substring(4, 6)) );
        else  sb.append(1) ;

        sb.append(",");
        if (isNumeric(  serviceCode.substring(6,8)  ))
             sb.append( Integer.parseInt(serviceCode.substring(6, 8)) );
        else  sb.append(1) ;


        // 4depth만 있는 경우 적용된다.
        if ( Integer.parseInt(  serviceCode.substring(8,10)  )  > 0    )  {
            sb.append(",");
            sb.append( Integer.parseInt(serviceCode.substring(8, 10)) );
        }

        return sb.toString() ;


    }

    /**
    * <pre>
    * 원 -> 만원 단위 변환 함수
    * 원단위 금액을 만원단위로 변경한다.
    * </pre>
    * @author silver_lion
    * @param money 원단위 금액
    * @return 만원단위 금액
    *
    */
    public static String getChangeMoneyUnit(String money){
        long smallUnit = Long.parseLong(money.trim());
        String returnBigUnitMoney = "";
        if(smallUnit < 10000){
            returnBigUnitMoney = String.valueOf(smallUnit);
        }else{
            returnBigUnitMoney = String.valueOf(smallUnit/10000);
        }
        return returnBigUnitMoney;
    }



    /**
    * 함수명 : XSS 공격을 위한 특수문자 제거 함수
    * @FuncDesc : 조회시 xss 공격을 위한 처리 - 입력불가 문자 삭제
    * @param str
    * @return String
    */
    public static String replaceXSSCharToBlank(String content) {
        if(content == null || content.trim().equals("")) return "";
        content = replace(content, "'", "");
        content = replace(content, "<", "");
        content = replace(content, ">", "");
        content = replace(content, "(", "");
        content = replace(content, ")", "");
        return content;
    }
    

    /**
    * Two Byte 데이터를 Trim한다
    */
    public static String getTwoByteTrim( String str) {
        if( str==null ) return "";
        str = (str.replace('　', ' ')).trim();
        return str;
    }
    
    public static String removeXSS(String strData)
    {
    	String[] arrSrcCode = {"<script>", "</script>"};    	
    	String[] arrTgtCode = {"&lt;script&gt;", "&lt;/script&gt;"};
    	
    	if ( strData == null || "".equals(strData) )
    		return strData;
    	
    	for (int nLoop=0; nLoop < arrSrcCode.length; nLoop++)
    	{
    		strData = strData.replaceAll(arrSrcCode[nLoop], arrTgtCode[nLoop]);
    	}
    	
    	return strData;
    	
    }
    
    /**
     * Method Summary. <br>
     * @param oData Object : 객체
     * @param sTrans String : null, "", "null"일 경우 변경할값
     * @return String
     */
    public static String getNullTrans(Object oData, String sTrans) {
        if (sTrans == null)
            sTrans = "";
        if (oData != null && !"".equals(oData) && !"null".equals(oData))
            return removeXSS(oData.toString().trim());

        return removeXSS(sTrans);
    }
    
    public static String getNullTrans(Object oData, int nTrans) {
        return getNullTrans(oData, Integer.toString(nTrans));
    }
    
    /**
     * Method Summary. <br>
     * @param oData Object : 객체
     * @return String
     */
    public static String nvl(Object oData) {
        return getNullTrans(oData, "");
    }    
   
    /**
     * Method Summary. <br>
     * @param oData Object : 객체
     * @param sTrans String : null, "", "null"일 경우 변경할값
     * @return String
     */
    public static String nvl(Object oData, String sTrans) {
    	return getNullTrans(oData, sTrans);
    }    
    
    /**
     * Method Summary. <br>
     * @param oData Object : 객체
     * @return String
     */
    public static String getNullTrans(Object oData) {
        return getNullTrans(oData, "");
    }
    
    public static String number_format(Object objNumber) {
        return number_format(objNumber, "#,##0");
    }

    public static String number_format(Object objNumber, String strFormat) {
        String strRetVal = "0";
        try {
            if (objNumber == null || "".equals(objNumber.toString()))
                return "0";
            String strNumber = objNumber.toString();

            Double dblNumber = Double.valueOf(strNumber);
            
            if (dblNumber <= 0) return "0";
            
            strFormat = ("".equals(strFormat)) ? "#,##0" : strFormat;

            DecimalFormat formatter = new DecimalFormat(strFormat);
            strRetVal = formatter.format(dblNumber);
        } catch (Exception e) {
            System.out.println("CommonUtil [number_format] " + e.toString());
        }
        return strRetVal;
    }

    public static String number_format(int objNumber) {
        
        return number_format(objNumber, "#,##0");
    }    
    
    public static String number_format(long objNumber) {
        
        return number_format(objNumber, "#,##0");
    }
    
    public static String number_format(float objNumber) {
        
        return number_format(objNumber, "#,##0");
    }
    
    public static String number_format(int objNumber, String strFormat) {
        String strRetVal = "0";
        try {
        	boolean isMinus=false;
            if (objNumber < 0){
            	objNumber=objNumber*-1;
            	isMinus=true;
            }else if(objNumber==0){
            	return "0";
            }
            String strNumber = Integer.toString(objNumber);

            Double dblNumber = Double.valueOf(strNumber);

            strFormat = ("".equals(strFormat)) ? "#,##0" : strFormat;

            DecimalFormat formatter = new DecimalFormat(strFormat);
            strRetVal = formatter.format(dblNumber);
            if(isMinus) strRetVal="-"+strRetVal;
        } catch (Exception e) {
            System.out.println("CommonUtil [number_format] " + e.toString());
        }
        return strRetVal;
    }
    
    /**
     * ibatis 의 리턴 XML을 합쳐진 xml 형태로 리턴한다.
     * @param list
     * @return
     */
    public static String toXml(List list){
		 StringBuffer xml = new StringBuffer();
		 xml.append("<DBLIST>");
		 if(list != null){
			 xml.append("<RECODE_COUNT>"+(list.size())+"</RECODE_COUNT>");
			 for(int i=0;i<list.size();i++){
				 xml.append(String.valueOf(list.get(i)).replaceAll("\\<(\\?)[xml](\\w+)(.*)(\\?)\\>", ""));
			 }
		 }
		 xml.append("</DBLIST>");
		 return xml.toString();
	 }
    
	/**
	* html에서 db에 데이터 입력시 replace 일괄처리
	*
	* @param String str
	* @return String
	*/
	public static String replaceHtmlDB(String content) {
		if(content == null || content.trim().equals("")) return "";

		content = replace(content, "'", "\'");
		content = replace(content, "\"", "&quot;");
		content = replace(content, "<", "&lt;");
		content = replace(content, ">", "&gt;");
		content = replace(content, "(", "&#40;");
		content = replace(content, ")", "&#41;");

		return content;
	}

	/**
	* db에서 html로 데이터 수정시 replace 일괄처리
	*
	* @param String str
	* @return String
	*/
	public static String replaceDBHtml(String content) {
		if(content == null || content.trim().equals("")) return "";

		content = replace(content, "\'", "'");
		content = replace(content, "&quot;", "\"");
		content = replace(content, "&lt;" , "<");
		content = replace(content, "&gt;" , ">");
		content = replace(content, "&#40;", "(");
		content = replace(content, "&#41;", ")");

		return content;
	}
    
	/**
	* \n을 <br>로 변경.
	*
	* @param String str
	* @return String
	*/
  	public static String nToBr(String asomething)
  	{
  		int i,len = asomething.length();
  		StringBuffer dest = new StringBuffer(len+500);

  		for( i = 0 ; i < len ; i++)	{
  			if( asomething.charAt(i) == '\n') {
  				dest.append("<br>");
  			} else if( asomething.charAt(i) == '\r') {
  				dest.append("");
  			} else {
  				dest.append(asomething.charAt(i));
  			}
  		}

  		return dest.toString();
  	}
  	
  	/**
	* <br>을 \n로
	*
	* @param String str
	* @return String
	*/
  	public  static String brToN(String asomething)
  	{
  		int len = asomething.length();
  		int index = -1 ;
  		int starter = 0;
  		String result ="";
  		while(true)
  		{

  			index = asomething.indexOf("<br>", starter);
  			if(index>-1) {
  				result += asomething.substring(starter, index)+"\n";
  				starter = index +4;
  			} else {
  				if(starter==0) {
  					result = asomething;
  				} else {
  					if(starter != len ) {
  						result += asomething.substring(starter, len);
  					}
  				}
  				break;
  			}
  		}

  		return result;
  	}
  	
	/**
	 * "&lt;br&gt;"을 "<br>" 문자로 치환
	 * @param obj
	 * @return
	 */
  	public static String charToBr(String content){
  		
  		if(content == null || content.trim().equals("")) return "";
  		
  		content = replace(content, "&lt;br&gt;", "<br>");  		
  		
  		return content;
  	}
  	
	/**
	 * "&lt;br&gt;"을 "\n" 문자로 치환
	 * @param obj
	 * @return
	 */
  	public static String charToN(String content){
  		
  		if(content == null || content.trim().equals("")) return "";
  		
  		content = replace(content, "&lt;br&gt;", "\n");  		
  		
  		return content;
  	}
    
  	/**
  	 * 랜덤으로 생성된 문자열을 반환 (숫자로만 생성됨)
  	 * @param maxLenth : 최대 길이
  	 * @return
  	 */
  	public static String random(int maxLength){
  		
  		Random rand=new Random();
  		StringBuffer result=new StringBuffer();
  		for(int i=0 ; i < maxLength; i++){
  			result.append(rand.nextInt(10));
  		}
  		
  		return result.toString();
  	}
  	
  	public static String randomAndAddZero(int maxLength) throws Exception{
  		Random rand=new Random();
  		int max = (int)Math.pow(10, maxLength);
  		return addZero((rand.nextInt(max))+"", maxLength);
  	}
  	
  	/**
  	 * 문자열을 분할하여 특정 위치 값을 반환한다. 만일 특정위치 값이 없을 경우 공값을 반환한다.
  	 * @Administrator
  	 * @2012. 9. 5.
  	 * @param data
  	 * @param divisionMark
  	 * @return
  	 */
  	public static String stringSplit(String data, String divisionMark, int index){
  		if(data==null) return "";
  		if(index < 0) return "";	//2019.02.14 integer overflow 보완 
  		
  		String segment="";
  		try{
  			segment=data.split(divisionMark)[index];	
  		}catch(ArrayIndexOutOfBoundsException e){
  			return "";
  		}
  		
  		if(segment==null) return "";
  		return segment;
  	}
  	
  	public static String timeFormat(Object minite) {
  		String timeMinite = minite+"";
  		try {
			int iTime = Integer.parseInt(timeMinite)/24;
			int iMin = Integer.parseInt(timeMinite)%24;
			if(iTime>0)
				timeMinite = iTime+"시간 ";
			if(iMin>0)
				timeMinite += (iMin+"분");
		} catch (NumberFormatException e) {
			return "숫자형식이 아닙니다.";
		}
  		return timeMinite;
  	}
  	
  	/**
     * Throws :<br>
     * Parameters : HttpServletRequest request <br>
     * Return Value : String <br>
     * 내용 : HttpServletRequest를 이용하여 파라메터를 리턴함 <br>
     */
    public static String getRequestQueryString(HttpServletRequest request) {

        HashMap map = new HashMap();

        String retQueryString = "";

        Map parameter = request.getParameterMap();
        Iterator it = parameter.keySet().iterator();
        Object paramKey = null;
        String[] paramValue = null;

        while (it.hasNext()) {
            paramKey = it.next();

            paramValue = (String[]) parameter.get(paramKey);

            for (int i = 0; i < paramValue.length; i++) {
                if (retQueryString.length() > 0)
                    retQueryString = retQueryString + "&amp;";

                retQueryString = retQueryString + paramKey + "=" + paramValue[i];
            }
        }
        
        return retQueryString;

    }
	/**
	 * 주민번호의 유효성을 체크한다.
	 * @kang
	 * @2012. 9. 26.
	 * @param juminNum
	 * @return
	 */
	public static boolean rrnCheck(String juminNum) {
		int[] check_num={2,3,4,5,6,7,0,8,9,2,3,4,5};
		int sum = 0;
		if(juminNum.length()==14){
			//주민번호 체크
			for(int i=0;i< juminNum.length()-1;i++){
				sum+=check_num[i]*(juminNum.charAt(i)-48);
			}
			int temp=11*(sum/11)+11-sum; 
			int bn=temp-10*(temp/10);               
			if(bn==juminNum.charAt(13)-48)    
				return true;  
			else                         
				return false;  
		}else{
			return false;
		}
		
	}	
	
	/**
	 * 파라미터 값들을 get 형태의 uri로 만든다.
	 * @Administrator
	 * @2012. 10. 22.
	 * @param params
	 * @return
	 */
	public static String makeURI(Map<String, String> params, String devide){
		StringBuffer uri=new StringBuffer();
		if(params !=null){
			Iterator<String> items=params.keySet().iterator();
			
			while(items.hasNext()){
				String name=items.next();
				try {
					uri.append(name+"="+String.valueOf(URLEncoder.encode(params.get(name)+"","utf-8")+devide));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return uri.toString();
	}
 
	public static String makeURI(Map<String, String> params){
		return makeURI(params, "&");
	}
	
	/**
	 * null 을 공백문자로 치환
	 * @param obj
	 * @return
	 */
	public static String isNullToStr(Object obj){
		String rStr = "";
		if(obj instanceof Integer){
			if(obj != null){
				rStr = String.valueOf(obj);
			}
			
		}else if(obj instanceof java.math.BigDecimal){
			if(obj != null){
				rStr = String.valueOf(obj);
			}
		}else if(obj instanceof String){
			if(obj != null && !((String) obj).trim().equals("")){
				rStr = ((String) obj).trim();
			}
		}else{
			if(obj != null){
				rStr = String.valueOf(obj);
			}
		}
		return rStr;
	}
	
	/**
	 * null 을 지정한 문자로 치환
	 * @param obj
	 * @return
	 */
	public static String isNullToStr(Object obj, String str){
		String rStr = str;
		if(obj instanceof Integer){
			if(obj != null){
				rStr = String.valueOf(obj);
			}
			
		}else if(obj instanceof java.math.BigDecimal){
			if(obj != null){
				rStr = String.valueOf(obj);
			}
		}else if(obj instanceof String){
			if(obj != null && !((String) obj).trim().equals("")){
				rStr = ((String) obj).trim();
			}
		}else{
			if(obj != null){
				rStr = String.valueOf(obj);
			}
		}
		return rStr;
	}

    private static final char[] zeroArray =
            "0000000000000000000000000000000000000000000000000000000000000000".toCharArray();

    /**
     * Pads the supplied String with 0's to the specified length and returns
     * the result as a new String. For example, if the initial String is
     * "9999" and the desired length is 8, the result would be "00009999".
     * This type of padding is useful for creating numerical values that need
     * to be stored and sorted as character data. Note: the current
     * implementation of this method allows for a maximum <tt>length</tt> of
     * 64.
     *
     * @param string the original String to pad.
     * @param length the desired length of the new padded String.
     * @return a new String padded with the required number of 0's.
     */
    public static String zeroPadString(String string, int length) {
        if (string == null || string.length() > length) {
            return string;
        }
        StringBuilder buf = new StringBuilder(length);
        buf.append(zeroArray, 0, length - string.length()).append(string);
        return buf.toString();
    }

	/**
	 * <pre>
	 * Checks if is regex pattern include.
	 * 전체 문자열 중에 일부 문자열이 정규식 패턴에 맞는지 체크한다.	
	 * ValidationUtil.isRegexPatternInclude("cabbbb", "a*b")=true
	 * </pre>
	 *
	 * @param str the str
	 * @param pattern the pattern
	 * @return true, if is regex pattern include
	 */
	public static boolean isRegexPatternInclude(String str, String pattern){
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(str);
		return m.find() ;
	}

	/**
	 * <pre>
	 * 사용자가 HTTP request를 통해 웹서버로 보낸 매개변수 값의 형식을  체크한다.
	 * </pre>
	 * 
	 * @param map 사용자가 HTTP request를 통해 웹서버로 보낸 매개변수
	 * @param div 체크할 형식 구분 
	 * <ul>
	 * 		<li>alpha-영문자</li> 
	 * 		<li>number-숫자</li>
	 * 		<li>boolean-Y/N</li>
	 * 		<li>date-날짜</li>
	 * 		<li>phone-전화번호</li>
	 * 		<li>hp-휴대폰</li>
	 * 		<li>email-이메일</li>
	 * </ul>
	 * @param args 체크할 매개변수
	 * @return
	 */
	public static boolean checkParameters(HashMap<String, Object> map, String div, String... args) {

		String regExp = "";
		if ("alpha".equals(div)) {
			regExp = "^[a-zA-Z]*$";
		} else if ("number".equals(div)) {
			regExp = "^[0-9]*$";
		} else if ("boolean".equals(div)) {
			regExp = "^[ynYN]{1}$"; // ^시작,$끝
		} else if ("date".equals(div)) {
			regExp = "^[0-9]{4}\\-[0-9]{1,2}\\-[0-9]{1,2}$"; // ^시작,$끝
		} else if ("phone".equals(div)) {
			regExp = "^[0-9]{2,3}\\-[0-9]{3,4}\\-[0-9]{4}$"; // ^시작,$끝
		} else if ("hp".equals(div)) {
			regExp = "^01(?:0|1|[6-9]){2,3}\\-[0-9]{3,4}\\-[0-9]{4}$"; // ^시작,$끝
		} else if ("email".equals(div)) {
			regExp = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+$  or  ^[_0-9a-zA-Z-]+@[0-9a-zA-Z-]+(.[_0-9a-zA-Z-]+)*$"; // ^시작,$끝
		} else {
			return true;
		}
		
		
		for (String key : args) {
			Object obj = map.get(key);
			
			if (obj != null) {
				if (String[].class.equals(obj.getClass())) {
					String[] arrVal = (String[]) obj;
					
					for (String str : arrVal) {
						
						if(!isRegexPatternInclude(str, regExp)) {
							return true;
						}
					}
				} else {
					String str = (String) obj;
					if(!isRegexPatternInclude(str, regExp)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	  * 글자가 한글인지 여부 확인
	  * @param data
	  * @return
	  */
	public static boolean isKorean(String data){
	  Pattern p = Pattern.compile("[가-힣]");
	  Matcher m = p.matcher(data);
	  if (m.matches()) {
	      return true;
	  } else {
//	      System.out.println("not match!");
	   return false;
	  }
	}
	
	/**
	 * UTF-8 인코딩시의 바이트 수를 계산하는 함수
	 * @param c
	 * @return
	 */
	protected static int getUTF8Count(char[] c) {
		int len = c.length;
		int count = 0;

		for (int i = 0; i < len; i++) {
			int ch = c[i];
			if (ch <= 0x7f) {
				count++;
			} else if (ch <= 0x7ff) {
				count += 2;
			} else {
				count += 3;
			}
		}
		return count;
	}
	
	
	/**
	 * Number 여부 확인
	 * 패턴 : 숫자
	 * 길이 : limit자 이하
	 * @param source
	 * @param limit
	 * @return
	 */
	public static boolean isNum(String source, int limit) {
		if(limit==0){
			limit = 10;
		}
		String pattern = "^[0-9]{0,"+limit+"}$";
		Pattern p = Pattern.compile(pattern);
		  Matcher m = p.matcher(source);
		  if (m.matches()) {
		      return true;
		  } else {
//		      System.out.println("not match!");
		   return false;
		  }
		
	}
	
	/**
	 * Password확인
	 * 패턴 : 영문으로 시작하여 영문, 숫자, 특수문자
	 * 길이 : 8자 이상 limit자 이하
	 * @param source
	 * @param limit
	 * @return
	 */
	public static boolean isPassword(String source, int limit) {
		if(limit<8){
			limit = 20;
		}
//		String pattern = "^[a-zA-Z]{1}[\\w\\W]{7,"+limit+"}$";
		String pattern =  "^.*(?=^.{8,"+limit+"}$)(?=^.[a-zA-Z])(?=.*?\\d)(?=.*?[a-zA-Z])(?=.*?[!@#$%^&+=]).*$";
		Pattern p = Pattern.compile(pattern);
		  Matcher m = p.matcher(source);
		  if (m.matches()) {
		      return true;
		  } else {
//		      System.out.println("not match!");
		   return false;
		  }
		
	}
	
	/**
	 * 사용자 이름 확인
	 * 패턴 : 한글이나 영어로 시작하여 한글,영문, 숫자
	 * 길이 : 2자 이상 limit자 이하
	 * @param source
	 * @param limit
	 * @return
	 */
	public static boolean isUsrName(String source, int limit) {
		if(limit<2){
			limit = 20;
		}
		String pattern = "^[a-zA-z가-힣]{1}[\\w가-힣]{1,"+(limit-1)+"}$";
		Pattern p = Pattern.compile(pattern);
		  Matcher m = p.matcher(source);
		  if (m.matches()) {
		      return true;
		  } else {
//		      System.out.println("not match!");
		   return false;
		  }
		
	}
	
	/**
	 * Url확인
	 * 패턴 : /로 시작하여 /,-,_,.,영문, 숫자
	 * 길이 : 3자 이상 100자 이하
	 * @param source
	 * @return
	 */
	public static boolean isUrl(String source) {
		String pattern = "^/[/\\-_.\\w]{2,99}$";
		Pattern p = Pattern.compile(pattern);
		  Matcher m = p.matcher(source);
		  if (m.matches()) {
		      return true;
		  } else {
//		      System.out.println("not match!");
		   return false;
		  }
	}
	
	/**
	 * 패턴 : 영문으로 시작해서 영문숫자 조합 
	 * 길이 : 3자 이상 20자 이하
	 * @param source
	 * @return
	 */
	public static boolean isCode(String source) {
		String pattern = "^[a-zA-Z]{1}[\\w]{2,19}$";
		Pattern p = Pattern.compile(pattern);
		  Matcher m = p.matcher(source);
		  if (m.matches()) {
		      return true;
		  } else {
//		      System.out.println("not match!");
		   return false;
		  }
	}
	
	
	/**
	 * email형식 확인
	 * 패턴 : (영어, 숫자, -,_)@(영어, 숫자, , _, -).(영어)
	 * 길이 : 각 자리 별 1자리 이상
	 * @param source
	 * @return
	 */
	public static boolean isEmail(String source) {
		String pattern = "^[_a-zA-Z0-9\\-]+@[._a-zA-Z0-9\\-]+[.][a-zA-Z]+$";
		Pattern p = Pattern.compile(pattern);
		  Matcher m = p.matcher(source);
		  if (m.matches()) {
		      return true;
		  } else {
//		      System.out.println("not match!");
		   return false;
		  }
	}
	
	/**
	 * 패턴 : 영어 대문자, 숫자
	 * 길이 : 1자 이상 length 이하
	 * @param source
	 * @param length
	 * @return
	 */
	public static boolean bigEngAndNumber(String source, int length) {
		String pattern = "^[A-Z0-9]{1,"+length+"}$";
		Pattern p = Pattern.compile(pattern);
		  Matcher m = p.matcher(source);
		  if (m.matches()) {
		      return true;
		  } else {
//		      System.out.println("not match!");
		   return false;
		  }
	}
	
	/**
	 * 패턴 : 영어, 숫자
	 * 길이 : 1자 이상 length 이하
	 * @param source
	 * @param length
	 * @return
	 */
	public static boolean engAndNumber(String source, int length) {
		String pattern = "^[a-zA-Z0-9]{1,"+length+"}$";
		Pattern p = Pattern.compile(pattern);
		  Matcher m = p.matcher(source);
		  if (m.matches()) {
		      return true;
		  } else {
//		      System.out.println("not match!");
		   return false;
		  }
	}
	
	
	/**
	 * 패턴 : 한자 일부, 영어, 한글, 숫자, _, -,+, / 공백
	 * 길이 : 1자 이상 length 이하 
	 * @param source
	 * @param length
	 * @return
	 */
	public static boolean codeName(String source, int length) {
		String pattern = "^([\\u4E00-\\u9FFF]|[A-Za-z가-힣0-9_\\-+ /]){1,"+length+"}$";
		Pattern p = Pattern.compile(pattern, Pattern.UNICODE_CASE);
		  Matcher m = p.matcher(source);
		  if (m.matches()) {
		      return true;
		  } else {
//		      System.out.println("not match!");
		   return false;
		  }
	}
	
	/**
	 * 패턴 : 한글이나 영문으로 시작하여 한글, 영문, 숫자 조합
	 * 길이 : 1자 이상20자 이하 
	 * @param source
	 * @return
	 */
	public static boolean isUsrId(String source) {
		String pattern = "^[a-zA-z가-힣]{1}[\\w가-힣]{1,19}$";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(source);
		if (m.matches()) {
		    return true;
		} else {
//			System.out.println("not match!");
		 return false;
		}
	}
	
	
	/**
	 * 패턴 : 숫자
	 * 길이 : 10자 
	 * @param source
	 * @return
	 */
	public static boolean isCmpyRegNo(String source) {
		String pattern = "^[0-9]{10}$";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(source);
		if (m.matches()) {
		    return true;
		} else {
//			System.out.println("not match!");
		 return false;
		}
	}
	
	public static boolean isMatch(String patternStr, String source){
		String pattern = ".*(?i)"+patternStr+".*";
		Pattern p =Pattern.compile(pattern);
		Matcher m = p.matcher(source);
		if(m.matches()){
			return true;
		}else {
			return false;
		}
	}
	public static String isNull(String param) {
		return param == null ? "" : param;
	}
	public static void main(String [] args){
//		String strPatternTest = "";
//		String strSource = "";
//		
//		strPatternTest = "phone";
//		strSource = "Phone";
//		System.out.println("isMatch("+strPatternTest+","+strSource+")="+StringUtil.isMatch(strPatternTest, strSource));
//		
//		strSource = "PHone";
//		System.out.println("isMatch("+strPatternTest+","+strSource+")="+StringUtil.isMatch(strPatternTest, strSource));
//		
//		strSource = "usrPhOne";
//		System.out.println("isMatch("+strPatternTest+","+strSource+")="+StringUtil.isMatch(strPatternTest, strSource));
//		
//		strSource = "usrPhOn";
//		System.out.println("isMatch("+strPatternTest+","+strSource+")="+StringUtil.isMatch(strPatternTest, strSource));
//		
//		strPatternTest = "";
//		strSource = "";
//		System.out.println("isMatch("+strPatternTest+","+strSource+")="+(StringUtil.isMatch(strPatternTest, strSource) ? "true" : "false"));
	
		String strPatternTest = "asdf1234!@#";
		int strSource = 20;
		System.out.println("isPassword("+strPatternTest+","+strSource+")="+(StringUtil.isPassword(strPatternTest, strSource) ? "true" : "false"));
		
		strPatternTest = "123ASD!@#";
		strSource = 20;
		System.out.println("isPassword("+strPatternTest+","+strSource+")="+(StringUtil.isPassword(strPatternTest, strSource) ? "true" : "false"));
		
		strPatternTest = "asdf1234!@#";
		strSource = 12;
		System.out.println("isPassword("+strPatternTest+","+strSource+")="+(StringUtil.isPassword(strPatternTest, strSource) ? "true" : "false"));
	
		strPatternTest = "asdf12345";
		strSource = 20;
		System.out.println("isPassword("+strPatternTest+","+strSource+")="+(StringUtil.isPassword(strPatternTest, strSource) ? "true" : "false"));
	
		
		try {
			Random rand=new Random();
			int max = (int)Math.pow(10, 6);
			System.out.println("max="+max+"\t"+"(rand.nextInt(max))="+(rand.nextInt(max)));
			max = (int)Math.pow(10, 6);
			System.out.println("max="+max+"\t"+"(rand.nextInt(max))="+(rand.nextInt(max)));
			max = (int)Math.pow(10, 7);
			System.out.println("max="+max+"\t"+"(rand.nextInt(max))="+(rand.nextInt(max)));
			max = (int)Math.pow(10, 8);
			System.out.println("max="+max+"\t"+"(rand.nextInt(max))="+(rand.nextInt(max)));
			for(int i=0; i<10 ; i++){
				System.out.print(random(8)+"\t");
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// encoding : UTF-8, EUC-KR
	public static String[] cutString(String raw, int len, String encoding) {
		if (raw == null) return null;
		
		String[] ary = null;

		try {
			byte[] rawBytes = raw.getBytes(encoding);
			int rawLength = rawBytes.length;
			
			int index = 0;
			int minus_byte_num = 0;
			int offset = 0;
			
			int hangul_byte_num = encoding.equals("UTF-8") ? 3 : 2;

			if(rawLength > len){
				int aryLength = (rawLength / len) + (rawLength % len != 0 ? 1 : 0);
				ary = new String[aryLength];

				for(int i=0; i<aryLength; i++){
					minus_byte_num = 0;
					offset = len;

					if(index + offset > rawBytes.length){
						offset = rawBytes.length - index;
					}

					for(int j=0; j<offset; j++){      
						if(((int)rawBytes[index + j] & 0x80) != 0){
							minus_byte_num ++;
						}
					}

					if(minus_byte_num % hangul_byte_num != 0){
						offset -= minus_byte_num % hangul_byte_num;
					}

					ary[i] = new String(rawBytes, index, offset, encoding);     
					index += offset ;
				}
		   } else {
			   ary = new String[]{raw};
		   }
		} catch(Exception e) {
			ary = null;
		}     
		return ary;
	}	
}
