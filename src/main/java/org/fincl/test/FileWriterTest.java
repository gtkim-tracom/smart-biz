package org.fincl.test;

import java.io.File;
import java.io.FileWriter;

import org.apache.commons.lang3.StringUtils;

public class FileWriterTest {
    
    public static void main(String[] args) {
        
        System.out.println("start----");
        for (int i = 0; i < 5000; i++) {
            String si = StringUtils.leftPad(i + "", 10, "0");
            String className = "TestRandom" + si;
            String beanName = "testRandom" + si;
            
            String txt = "package org.fincl.miss.service.biz;\n\n";
            txt += "import org.fincl.miss.server.channel.ExtChannelManager;\n";
            txt += "import org.springframework.beans.factory.annotation.Autowired;\n";
            txt += "import org.springframework.stereotype.Service;\n\n";
            
            txt += "@Service(\"" + beanName + "\")\n";
            txt += "public class " + className + " {\n";
            
            txt += "@Autowired\n";
            txt += "ExtChannelManager channerManager;\n\n";
            
            txt += "public void aaa1() {\n";
            txt += "System.out.println(\"=====invoke1111\");\n";
            txt += "System.out.println(\"channerManager::\" + this + \"==>\" +channerManager);\n";
            txt += "System.out.println(\"=====invoke2222333333\");\n";
            txt += "}\n\n";
            
            txt += "public void aaa2() {\n\n";
            
            txt += "}\n";
            txt += "}\n";
            
            String fileName = "D:/eGovFrameDev/workspace/miss-biz/src/main/java/org/fincl/miss/service/biz/" + className + ".java";
            
            try {
                
                // 파일 객체 생성
                File file = new File(fileName);
                
                // true 지정시 파일의 기존 내용에 이어서 작성
                FileWriter fw = new FileWriter(file, true);
                
                // 파일안에 문자열 쓰기
                fw.write(txt);
                fw.flush();
                
                // 객체 닫기
                fw.close();
                
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            
            if (i % 1000 == 0) {
                System.out.println("ing----[" + i + "]");
            }
        }
        
        System.out.println("end----");
    }
}