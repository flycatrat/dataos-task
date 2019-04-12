package com.dataos.task;

import com.dataos.task.util.GenerateScript;
import com.dataos.task.util.TemplateContext;
import freemarker.template.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataTaskApplicationTests {
    @Autowired
    GenerateScript generateScript;

	@Test
	public void contextLoads() throws IOException {
        TemplateContext tc=new TemplateContext();
        tc.setTaskId("1");
        tc.setTaskRootPath("/Users/libing/workspace/");
        tc.setTaskTargetPath("dataos/template/");
        tc.setTaskName("mysql_to_hive.load.incr_d_dbs.import.sh");
        tc.setTemplateName("load_import_full_sh.ftl");

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("constant","constant");
        map.put("defaultDate","constant");
        map.put("transDate","constant");
        map.put("paramCN","2");
        map.put("from_db_type","mysql");
        map.put("to_db_type","hive");
        map.put("from_db","hive");
        map.put("from_table","dbs");
        map.put("to_db","dataos_ods");
        map.put("to_table","dbs");
        String q_sql="SELECT * FROM\n" +
                " (SELECT *\n" +
                " FROM $from_db.$from_table m1\n" +
                " ) a WHERE \\$CONDITIONS";
        map.put("q_sql",q_sql);
        map.put("map_Num","1");
        map.put("primaryKey","db_id");
        map.put("partition_key","dt");
        tc.setMap(map);
        try {
            generateScript.doGenerateScript(tc);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
	}

}
