#ifndef DEMO_ICE
#define DEMO_ICE

module demoice{
    module demo{
        sequence<string> StringList;
        dictionary<string,string> AffixParamMap;

        class SearchCondition{
            string fieldName; //字段名列表，使用逗号分割
            string keyword;   //查询条件
            string demandType;//字符串: -eq(等于) -ne(不等于) -in(值列表之一，对个值用','分割)
                              // 数值:  -eq(等于) -ne(不等于) -in(值列表之一，对个值用','分割) -bt(区间，包含边界) -lt(小于) -gt(大于) -le(小于等于) -ge(大于等于)

        };
        sequence<SearchCondition> SearchConditionList;

        class AggregationCondition{
            string aggsType;                            //聚合类型，精确类型聚会(exact) 时间范围类型聚合(dateRange) 数值范围类型聚合(numericRange) 支持后续更多聚合类型扩展
            string aggsInterval;                        //聚合间隔，仅范围类型聚合需要，时间范围类型聚合间隔单位s(秒)、m(分钟)、h(小时)、d(天)，单位前面可根据聚合需求添加整型数值(要求大于0)，数值范围类型聚合间隔可设置为数值类型(要求大于0)
            string aggsField;                           //被聚合应用的索引字段
            AggregationCondition nestedAggsCondition;  //嵌套聚合
        };

        class ListRequest{
            string dataType; //数据类型，多个值用','分割
            string id;       //实体id，必填
        };

        class SearchRequest{
            string dataType;                     //数据类型
            string expressionStr;                //查询条件表达式，表达式中参数表示在参数列表中的下标值，从0开始，格式如下“0&((1&2)|(3&4))&!5”
            SearchConditionList searchConditions;//查询参数列表
            AffixParamMap afxParamMap;           //附加信息(优先级、单库结果数、总结果数、返回的结果数据类型[如检索结果，数据量，码址数量，码址列表])
        };

        class StatRequest{
            string dataType;
            string expressionStr;
            SearchConditionList searchConditions;
            AffixParamMap afxParamMap;
            AggregationCondition aggsCondition;
        };

        class AggregationResult{
            string key;
            long docCount;
            AggregationResult nestedAggsResult;
        };

        sequence<AggregationResult> AggregationResultList;

        class ListResponse{
            string retCode;          // >=0 成功, -1 失败
            string errorCode;       // 当retCode=-1时生效，码表参加《业务应用结构.docx》
            string errorMessage;    // 当retCode=-1时生效，码表参加《业务应用结构.docx》
            string rawxx;           // 结果字符串
        };

        class SearchResponse{
            string retCode;
            string errorCode;
            string errorMessage;
            StringList rawxxs;  // 结果字符串数组
            bool isLast;        // true 结束，false 未结束
        };

        class StatResponse{
            string retCode;
            string errorCode;
            string errorMessage;
            AggregationResultList aggsResults;
        };

        struct DataBlockChangeRecord{
            string blockname;  // 文件名
            string bgName;     //大组名
            string server;    // 存储服务器
            string path;     // 在存储服务器的存储目录
            string datatype; // 数据类型
            string date;     // yyyyMMdd
            int state;      // -1 del, 1 add, 0 同步全部登记信息时的标记

        };

        sequence<DataBlockChangeRecord> DataBlockChangeRecordList;

        interface DemoProxy{

            /**
              * @Deprecated 黑网使用，后续以接口getStatResult替代
              *
              * 获取统计分析结果，同步接口
              *
              * @param request 统计条件，json格式
              * @return 统计结果，json格式
              */
            string deliverRequest(string request);


            /**
              * 获取特定实体，同步接口
              *
              * @param request 列举条件
              * @return 列举结果
              */
            ListResponse listEntity(ListRequest request);

            /**
              * 批量获取检索结果，同步接口
              *
              * @param request   检索条件
              * @param startPos  当前批次获取结果开始位置
              * @param batchSize 当前批次获取结果大小
              * @return 检索结果
              */
            SearchResponse getSearchResult(SearchRequest request, int startPos, int batchSize);

            /**
              * SQLite数据库中批量获取检索结果，同步接口
              *
              * @param request   检索条件
              * @param startPos  当前批次获取结果开始位置
              * @param batchSize 当前批次获取结果大小
              * @return 检索结果
              */
            SearchResponse getSearchResultFromSQLite(SearchRequest request, int startPos, int batchSize);

            /**
              * 获取统计分析结果，同步接口(暂不处理)
              *
              * @param request 统计条件
              * @return 统计结果
              */
            StatResponse getStatResult(StatRequest request);

            /**
              * 索引库等级信息，同步接口
              *
              * @param changeset           索引登记信息变更集
              * @param curSequenceNumber   当前序号数
              * @param totalSequenceNumber 总序号数
              * @return 同步成功与否
              */
            bool notifyBlockChange(DataBlockChangeRecordList changeset, int curSequenceNumber, int totalSequenceNumber);

        };

    };
};
#endif