# screen-filter
眼睛保护APP小应用
1.功能介绍：
屏幕滤镜，大致原理是在APP的window层加view，相似于屏幕的滤镜，包括调节滤镜颜色，调节亮度，浓度，
自动滤镜功能，基于Service开发，后台开启Service，在Service使用BroadCastReceiver监测手机时间，当接收到时钟广播时判断是否处于特定时段（未实现）
用眼时长提醒：基于Service开发，后台开启Service，在Service使用BroadCastReceiver中监听手机使用时长。
应用统计：使用AndroidAccess框架  AndroidProcesses项目可用于获取当前系统正在运行的应用的信息，针对Android5.0以后使用。
眼保健操：这个没啥可说的，，加图片在上面显示（可扩展为语音播报，结合智能语音机器人等项目，可使用百度语音sdk或者科大讯飞sdk实现）
科学训练：日本某某13点训练法，使用自定义view实现
视力统计：基于Android SQLite数据库实现功能，借助图表画出折线图


2.项目框架
https://github.com/jaredrummler/AndroidProcesses.git   ndroidAccess框架，实现应用统计功能
https://github.com/hwding/screen-filter.git   实现屏幕滤镜效果
https://github.com/PhilJay/MPAndroidChart      MPChart框架，实现折线图Android很强的图表库
