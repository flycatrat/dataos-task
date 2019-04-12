#!/bin/bash

###########################################################################################################################
#description ： import data from ${from_db_type} to ${to_db_type} table with partition[使用SQOOP]
#
###########################################################################################################################

set -e

. "/etc/profile"
. "/home/$USER/.bashrc"
. "$DW_DATAOS_HOME/dataos_common/.common_profile"

curDir=$(cd `dirname $0`; pwd)
scriptName=`basename $0`

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



echo "constant params ${constant};date params  ${defaultDate}"

echo "load day :${defaultDate}"

# ${from_db_type} 源表 "hive"
from_db="${from_db}"
#"DBS"
from_table="${from_table}"

# ${to_db_type} 表 写入的目的地 "dataos_ods"
to_db="${to_db}"
# "dbs"
to_table="${to_table}"


#可根据文件名称生成
className="Load${to_db}${to_table}"

#oracle数据库连接信息
properties="$COMMON_ETC_PATH/db.hive.${from_db_type}.properties"

#查询数据的sql
#SELECT * FROM
#(SELECT *
#FROM $from_db.$from_table m1
#) a WHERE \$CONDITIONS
query="${q_sql}
"
#mapNum 1
mapNum="${map_Num}"
#map 数据分区读取的数据块划分需要用到的字段
#primaryKey="DB_ID"
primaryKey="${primaryKey}"
#partitionContent="incr_dt='$run_day'" 分区表达式 这个表达式可以通过页面直接获取
partitionContent="${partition_key}='$bizdate'"
echo $partitionContent

echo "$query"

bash $COMMON_BIN_PATH/commonFullTable.sh $properties "$to_db" "$to_table" "$from_db" "$from_table" "$query" "$mapNum" "$primaryKey" "$partitionContent" "$className"
