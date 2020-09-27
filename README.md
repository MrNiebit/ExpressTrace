# 功能介绍
## 快递查询
-  通过爬取百度，获取物流信息
- 支持大部分快递，顺丰快递暂不支持。
## 快递跟踪
- 每间隔25分钟查询，并记录。
- 当物流更新，会以邮箱的方式发送到邮箱。

# 使用教程

## 后台部署

- 导入数据库
- 修改配置文件中的数据库账号和密码
- 使用smtp，将邮箱账号和smtp密钥填入。

- 直接运行即可

`/trace/express?expressName=快递名字&expressNumber=单号`查询物流信息。其中快递名字可以为auto
`/trace/addTrace` 物流跟踪

## 前台部署
最好装个node环境
使用npm。
进入前台目录终端运行
```bash
npm install
```

安装完成之后，运行

```bash
npm run dev
```

# 关于

- 代码并不规范
- 功能基本上能够使用

还在学习中... 欢迎给出建议
一起提升。

# 新增Docker远程部署

## 本机设置
1. idea安装Docker插件，一般是自带 的。
2. 在项目根目录下创建Dockerfile文件，文件名一般固定为Dockerfile
```dockerfile
FROM java:8
MAINTAINER gitsilence <gitsilence@lacknb.cn>
EXPOSE 8080
ADD *.jar app.jar
ENTRYPOINT ["java", "-Dfile.encoding=utf-8" ,"-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]

```
3. maven配置插件
```xml
            <plugin>
                <!--新增的docker maven插件-->
                <groupId>com.spotify</groupId>
                <artifactId>docker-maven-plugin</artifactId>
                <version>0.4.13</version>
                <configuration>
                    <!--dockerDirectory  指定 dockerfile的 位置 -->
<!--                    <dockerDirectory>src/main/docker</dockerDirectory>-->
                    <!--镜像名字-->
                    <imageName>app.jar<!--${docker.image.prefix}/${project.artifactId}--></imageName>
                    <!--DokcerFile文件地址-->
                    <dockerDirectory>/slm/</dockerDirectory>
                    <resources>
                        <resource>
                            <targetPath>/</targetPath>
                            <directory>${project.build.directory}</directory>
                            <include>${project.build.finalName}.jar</include>
                        </resource>
                    </resources>
                </configuration>
            </plugin>

            <plugin>
                <artifactId>maven-antrun-plugin</artifactId>

                <!--
                    phase定义成package，goal定义成run，是为了在运行 " mvn package "的时候自动
                    执行第一个execution节点下的ant任务
                -->

                <executions>
                    <execution>
                        <phase>package</phase>
                        <configuration>
                            <tasks>
                                <copy todir="${project.basedir}" file="target/${project.artifactId}-${project.version}.${project.packaging}">

                                </copy>
                            </tasks>
                        </configuration>
                        <goals>
                            <goal>run</goal>
                        </goals>
                    </execution>
                </executions>

                <!--
                    [INFO] -maven-antrun-plugin:1.8:run (default) @ backend
                    [WARNING] Parameter tasks is deprecated, use target instead
                    [INFO] Executing tasks
                 [copy] Copying 1 file to E:\javaProject\ExpressTrace\backend
                -->

            </plugin>
```

## docker端的设置

```bash
vi /usr/lib/systemd/system/docker.service
```

在这一行 后面新增 -H 0.0.0.0:2375
```text
ExecStart=/usr/bin/dockerd -H fd:// --containerd=/run/containerd/containerd.sock -H tcp://0.0.0.0:2375
```

然后重启docker

```bash
systemctl restart docker
```
重新加载配置文件
```bash
systemctl daemon-reload
```

## 使用idea上的插件连接docker

![0FOpQ0.png](https://s1.ax1x.com/2020/09/27/0FOpQ0.png)

然后允许 dockerfile文件 build 构建