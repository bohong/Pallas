# Pallas代码运行指引

## 概述
基于[Maven](http://maven.apache.org/)构建。

项目整体划分为以下模块：

  - 前后端分离的Pallas-console模块：pallas-console；pallas-console-web
  - 封装Dao/Service层：pallas-core；pallas-core-open
  - 封装公共类：pallas-common
  - 基于Netty的ES代理模块：pallas-search
  - 客户端：pallas-rest-client
  - 插件：pallas-plugin

## 1 Pallas core

  - 封装Dao/Service层
  
  - pallas-console,pallas-search都依赖它，因此需先构建pallas-core,pallas-core-open
  
  - 构建命令：mvn clean package

## 2 Pallas console

  - 依赖pallas-core,pallas-core-open
  
  - 以maven方式运行：-Dspring.profiles.active=development -Dpallas.stdout=true -Dpallas.db.profiles.active=h2
  
    - 数据源：支持mysql，h2
    
    - pallas.db.profiles.active：如果不指定，为mysql；如若指定为h2，默认为内存模式，可在properties文件中修改为文件或其他模式
    
    - 运行：借助Eclipse，IDEA等开发集成环境
    
  - 启动会监听8080端口
  
## 3 Pallas console web

  - 基于[vue](https://cn.vuejs.org/)的前端代码

  - 环境搭建
  
    - 安装Node（node --version查看node是否成功安装，npm --version 查看npm是否成功安装，新版本node已经集合了npm，如未安装npm请翻阅教程重新安装）
    
    - 安装yarn( yarn --version 查看yarn是否成功安装 )
    
    - 前端开发目录如D:\project\pallas_web，运行npm install或yarn install(安装项目自动化工程所需插件)
    
  - 运行
  
    - 键入npm run dev或yarn dev命令运行本地项目（如需打包请键入npm run build 或 yarn build），命令完成后会自动打开浏览器进入(http://localhost:8081)
    
## 4 Pallas search

  - 依赖pallas-core，pallas-common
  
    - 运行前需先构建pallas-core，构建命令：mvn clean package
   
  - 启动
  
    - 先确认pallas.vip.vip.com域名指向dev环境10.199.XXX.XXX pallas.vip.vip.com，否则会上报到错误的环境产生垃圾信息。
    
    - Eclipse，IDEA或者其他集成环境：
      
      - 以main方式启动com.vip.pallas.search.launch.Startup
      
      - 添加-Dpallas.search.cluster=pppp 所属集群，随便填
      
      - 添加-Dpallas.stdout=true -Dpallas.search.port=9225  监听端口
      
      - 添加-DVIP_PALLAS_CONSOLE_REST_URL=http://localhost:8080/pallas 
      
      - 添加-Dpallas.console.upload_url=http://localhost:8080/pallas/ss/upsert.json ，向console上报search信息
      
  - 启动后打开pallas.vip.vip.com，便可在代理管理tab找到你的机器并且是上线状态
  
## 5 pallas rest client

  - 详细使用方式见SDK设计
  
  - 使用前切记hosts中配置pallas.vip.vip.com

## 6 pallas plugin

  - 详细使用方式见插件管理中的pallas-plugin开发与使用模块。


  
  
  