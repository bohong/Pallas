业务方在与pallas团队初步沟通后，需要发送邮件申请接入。邮件内容将作为备案用途。邮件其内容如下：


**发送：薛珂<[dylan.xue@vipshop.com](mailto:dylan.xue@vipshop.com)>**

**邮件名：xx项目组申请接入pallas**

**抄送给双方团队相关人员，这是pallas开发测试运维成员： owen.li@vipshop.com, ivy01.li@vipshop.com, chembo.huang@vipshop.com, jamin.li@vipshop.com, gier.cai@vipshop.com,mason.chan@vipshop.com,yy.xu@vipshop.com,alfred.wang@vipshop.com, tanner.cai@vipshop.com,haiming04.wang@vipshop.com**

**邮件内容包含以下内容：**

*   **项目负责人：**
*   **是否核心域：**
*   **数据源与数据量(目前支持mysql单表、同构多表、vms消息)：**
*   **数据增长(包括增删改)：**
    
*   **表字段数量(或者一条记录大小)：**
*   **预计上线时间：**
*   **希望部署机房(尽量同机房调用接口)：**
*   **是否能降级:**
*   **是否需要跨机房高可用: (默认同机房高可用: 主从分片）**
*   **接口1  
    **
    *   **描述：**
    *   **SQL：**
    *   **qps：**
*   **接口2**
    *   **描述：**
    *   **sql：**
    *   **qps：**


示例

*   项目负责人：薛珂
*   是否核心域：是
*   数据源与数据量：mysql单表，5亿
*   数据增长(包括增删改)：1kw/week
    
*   表字段数量：18
*   预计上线时间：8月底
*   希望部署机房(尽量同机房调用接口)：佛山
*   是否能降级：是
*   是否需要跨机房高可用：否
*   接口1
    *   描述：根据300个id找是否在售
    *   sql：select id from xx where status = 1 and id in (300 ids) and sell\_time\_to > now
    *   qps：平时1w/s 大促5w/s