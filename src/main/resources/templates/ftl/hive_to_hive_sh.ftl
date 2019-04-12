#!/bin/bash

set -e

. "/etc/profile"
. "/home/$USER/.bashrc"
. "$DW_DATAOS_HOME/dataos_common/.common_profile"

curDir=$(cd `dirname $0`; pwd)
cd $curDir

#db_type='oracle'  常量变量
${constant}

#${paramCN} 表示通过job传递过来的参数，如果paramCN=<0,if内容需要
if [ $# -lt ${paramCN} ]; then
   #run_day=$(date -d "-1 day" "+%Y-%m-%d")，系统默认参数值
   ${defaultDate}
else
   #run_day=$1  通过job传递的参数
   ${transDate}
fi

shellMap.put("paramCN", paramCN);
		shellMap.put("defaultDate", defaultDate.toString());
		shellMap.put("transDate", transDate.toString());
		shellMap.put("jobDate", jobDate.toString());
		shellMap.put("constant", constant.toString());
		shellMap.put("sqlScript", sqlScript.toString());
		
echo "${sqlScript}"

echo "Start to compute merchant cdpm outer account daily of${sqlScript}"

#.sql文件的名称和shell文件的名称保持一致
hive_script="../script/${SQLFILENAME}"
#--define run_day=${sqlScript} sql文件传递参数格式
echo "hive -v -f ${hive_script} ${sqlScript}"
hive -v -f ${hive_script}   ${sqlScript}

echo "Merchant cdpm outer account daily of $run_day done!"
