# OAT开源审查工具<a name="ZH-CN_TOPIC_0000001106844024"></a>

- [简介](#section11660541593)
- [特性介绍](#section367519246426)
- [目录](#section161941989596)
- [约束](#section119744591305)
- [编译构建](#section137768191623)
- [CI集成](#section7899558173218)
- [运行](#section1634911263317)
    - [单项目模式运行](#section1771013213818)
    - [批量模式运行](#section20292217143516)

- [默认规则配置](#section729883153314)
- [开源仓规则配置](#section119891146124010)
- [OAT问题确认](#section136713102427)
- [Committer审核代码](#section77489114429)
- [许可证](#section126611612164217)

## 简介<a name="section11660541593"></a>

OAT（OSS Audit Tool）是OpenHarmony社区的自动化开源审视工具，用于帮助开发人员基于自定义的规则自动扫描开源仓代码，识别不符合预定规则的代码并输出扫描报告。

OAT遵从极简、实用的原则，不提供复杂的图形化展示界面，无需部署并维护额外的数据库环境，不提供复杂的操作流程从而避免对用户的业务主流程造成影响，而是聚焦于提供灵活的源码扫描方式、高度可定制的许可证匹配及审查等规则、精简的报告数据格式及灵活的问题过滤与确认规则，满足用户快速部署、快速定义规则并扫描、快速确认问题并固化确认结果等诉求。


## 特性介绍<a name="section367519246426"></a>

OAT提供了灵活的可扩展能力，用户可以扩展代码扫描及报告逻辑，针对OpenHarmony社区的开发规范，OAT缺省支持如下几种类型问题的扫描：

- 二进制文件
- 许可证兼容性
- 源代码许可头
- 源代码版权头
- 项目许可证文件
- 项目README文件
- 项目README.OpenSource文件
- Include/Import依赖

所有上述扫描都支持自定义扫描规则，如指定路径下代码采用什么许可证、版权头等。OAT支持全局缺省的规则以及项目级规则，在运行期会将两者合并，满足不同开源仓许可证、版权信息不同以及同一开源仓在演进过程中许可证变化的业务诉求。

## 目录<a name="section161941989596"></a>

```
/tools/oat
├── src         # 工具代码
├── template    # 工具配置文件模板 
```

## 约束<a name="section119744591305"></a>

OAT基于Java语言开发，需要在Java 8以上Java环境运行。

## 编译构建<a name="section137768191623"></a>

OAT是基于Maven 3.5.2编译构建的，请确保您的环境已安装Maven，然后通过如下方式完成构建：

```
mvn package
```

使用上述命令完成构建后，在target目录下会生成ohos\_ossaudittool-xx.jar，并会将其依赖的库复制到target/libs目录下，您需要将上述两部分一起复制到您需要运行的环境中，并维持OAT的依赖库在其同级的libs目录中。

注意：构建过程需要从Maven远程仓库中下载OAT的依赖库，因此，请确保构建过程中可以正常访问Maven远程仓库，该仓库地址在您Maven的settings.xml文件中指定。

## CI集成<a name="section7899558173218"></a>

OpenHarmony社区开源项目采用了多种编程语言、多种构建工具，OAT只是检测开源仓源码文本，与语言是解耦的，因此并未直接集成到各开源仓的构建逻辑中，而是作为通用工具集成到OpenHarmony社区门禁，各开源仓代码提交后可自动触发OAT检测并输出报告，若您需要集成到项目的构建过程中，可参考[单项目模式运行](#section1771013213818)
运行参数快速集成。

## 运行<a name="section1634911263317"></a>

OAT是基于Java语言开发的，因此可以运行在多种操作系统中，请确保您的环境已安装JDK8以上版本，OAT支持批量以及单项目两种运行模式：

### 单项目模式运行<a name="section1771013213818"></a>

```
java -jar ohos_ossaudittool-xx.jar -mode s -s sourcedir -r reportdir -n nameOfRepo
                                                                                         
options:                                                                                                                                
 -mode <arg>     Operating mode, 's' for check single project                                                                           
 -h              Help message                                                                                                           
 -l              Log switch, used to enable the logger                                                                                  
 -s <arg>        Source code repository path, eg: c:/test/                                                                              
 -r <arg>        Report file folder, eg: c:/oatresult/                                                                                  
 -n <arg>        Name of repository, used to match the default policy                                                                   
 -w <arg>        Check way, 0 means full check, 1 means only check the file list                                                        
 -f <arg>        File list to check, separated by |                                                                                     
 -k              Trace skipped files and ignored files
 -g              Ignore project OAT configuration
 -p              Ignore project OAT policy
 -policy <arg>   Specify check policy rules to replace the tool's default rules.
                 eg:repotype:upstream; license:Apache-2.0@dirA/.*|MIT@dirB/.*|BSD@dirC/.*;copyright:Huawei Device Co.,
                 Ltd.@dirA/.*;filename:README.md@projectroot;filetype:!binary~must|!archive~must;compatibility:Apache-2.0
                 Note:
                 repotype:'upstreaam' means 3rd software, 'dev' means self developed
                 license: used to check license header
                 copyright: used to check copyright header
                 filename: used to check whether there is the specified file in the specified directory
                 filetype: used to check where there are some binary or archive files
                 compatibility: used to check license compatibility
 -filter <arg>   Specify filtering rules to filter some files or directories that do not need to be checked.
                 eg:filename:.*.dat|.*.rar; filepath:projectroot/target/.*

```

其中第一个参数是待扫描项目的根目录路径，第二个参数为报告输出路径，本模式采用resources/OAT-Default.xml中的定义作为默认规则，如果默认规则不满足业务要求，您同样可以在开源仓根目录放置一个命名为 "OAT.xml"
的文件作为开源仓规则，详情参见[开源仓规则配置](#section119891146124010)章节的描述。

OAT已集成到OpenHarmony社区门禁，门禁被触发时即以此模式在运行，您可以在resources/OAT-Default.xml中查看工具的默认规则。

以windows环境下，根目录路径为E:\example的项目为例，使用OAT发行版v2.0.0-beta.2的运行指令和效果如下：

```
E:\example>java -jar ohos_ossaudittool-2.0.0-beta.2.jar -mode s -s E:\example -r E:\example -n example
--------------------------------------------------------------------------
OpenHarmony OSS Audit Tool
Copyright (C) 2021-2022 Huawei Device Co., Ltd.
If you have any questions or concerns, please create issue at https://gitee.com/openharmony-sig/tools_oat/issues
--------------------------------------------------------------------------
Result file path:       E:\example\single\PlainReport_example.txt
Result file path:       E:\example\single\PlainReport_example_Detail.txt
example cost time(Analyse|Report):      2|0
```

运行成功后会生成PlainReport_example.txt和PlainReport_example_Detail.txt两个分析报告文件，前者是对项目问题的简要总结，后者包括配置信息和项目问题的详细说明，可通过查看以上两个文件改正项目问题。

### 批量模式运行<a name="section20292217143516"></a>

```
java -jar ohos_ossaudittool-xx.jar -mode m -i OAT-ALL.xml

options:
 -mode <arg>     Operating mode, 'm' for check multiple projects
 -h              Help message
 -l              Log switch, used to enable the logger
 -i <arg>        OAT.xml file path, default vaule is OAT.xml in the running path
 -r <arg>        Report file folder, eg: c:/oatresult/
 -k              Trace skipped files and ignored files
 -g              Ignore project OAT configuration
 -p              Ignore project OAT policy
 -policy <arg>   Specify check policy rules to replace the tool's default rules,
                 eg:repotype:upstream; license:Apache-2.0@dirA/.*|MIT@dirB/.*|BSD@dirC/.*;copyright:Huawei Device Co.,
                 Ltd.@dirA/.*;filename:README.md@projectroot;filetype:!binary~must|!archive~must;compatibility:Apache-2.0
                 Note:
                 repotype:'upstreaam' means 3rd software, 'dev' means self developed
                 license: used to check license header
                 copyright: used to check copyright header
                 filename: used to check whether there is the specified file in the specified directory
                 filetype: used to check where there are some binary or archive files
                 compatibility: used to check license compatibility
 -filter <arg>   Specify filtering rules to filter some files or directories that do not need to be checked.
                 eg:filename:.*.dat|.*.rar; filepath:projectroot/target/.*

```

批量模式生成的报告位于OAT的运行目录，其中OAT-ALL.xml用于配置待扫描的项目清单及默认的许可证、Copyright等策略及默认的过滤规则，您可基于resources/OAT-Default.xml修改生成OAT-ALL.xml。

注意：

- 在tasklist中新增task并配置您要扫描的project信息，不要修改默认的defaultTask。
- 可以使用 **java -jar ohos_ossaudittool-VERSION.jar -s sourcedir -c** 命令行生成该源码目录下所有projects，结果会写入OAT.log文件，您可参考这些信息来配置OAT-ALL.xml中新增的task。

扫描项目及路径配置说明：

1、basedir用于配置待扫描代码仓的上级目录，请配置绝对路径：

```
<basedir>/home/cyx/OpenHarmony/</basedir>
```

2、tasklist用于配置扫描任务，每个task会启动一个单独的线程进行扫描并生成单独的报告，对于代码量多的仓建议配置到单独的Task以提升扫描效率：

```
<tasklist>
    <task name="defaultTask" policy="defaultPolicy" filefilter="defaultFilter" desc="">
        <project name="hmf/kernel/liteos-a" path="kernel/liteos_a"/>
    </task>
</tasklist>
```

3、project用于配置开源仓，其path字段用于指定开源仓在basedir目录下的路径，请确保basedir+projectpath为该project的根目录路径。

4、project也可以配置policy以及filefilter属性，但默认会继承其对应task的policy及filefilter属性值。

5、policy用于配置许可证、版权头等规则，filefilter用于配置哪些文件不用扫描，详情参见[默认规则配置](#section729883153314)章节的描述。

6、如果默认规则不满足业务要求，您可以在各开源仓根目录放置一个命名为 "OAT.xml"的文件作为开源仓规则，详情参见[开源仓规则配置](#section119891146124010)配置章节的描述。

## 默认规则配置<a name="section729883153314"></a>

默认规则配置包含策略配置policylist、过滤配置filefilterlist、许可证匹配规则配置licensematcherlist三大部分内容。

- 策略配置

策略配置用于配置OAT审查代码的实际规则，所有project都会绑定到一个具体的policy，如下为policy的配置样例：

```
<policy name="defaultPolicy" desc="" >
    <policyitem type="compatibility" name="Apache" path=".*" rule="may" group="defaultGroup" filefilter="defaultPolicyFilter" desc=""/>
    <policyitem type="license" name="Apache-2.0" path="!.*LICENSE" rule="may" group="defaultGroup" filefilter="defaultPolicyFilter" desc=""/>
    <policyitem type="copyright" name="Company Co., Ltd." path=".*" rule="may" group="defaultGroup" filefilter="copyrightPolicyFilter" desc=""/>
    <policyitem type="filename" name="LICENSE" path=".*/adapter/" rule="may" group="defaultGroup" filefilter="licenseFileNamePolicyFilter" desc=""/>
    <policyitem type="filename" name="LICENSE" path="projectroot" rule="may" group="defaultGroup" filefilter="licenseFileNamePolicyFilter" desc=""/>
    <policyitem type="filename" name="README" path="projectroot" rule="may" group="defaultGroup" filefilter="readmeFileNamePolicyFilter" desc=""/>
    <policyitem type="filename" name="README.OpenSource" path="projectroot" rule="may" group="defaultGroup" filefilter="readmeFileNamePolicyFilter" desc=""/>
    <policyitem type="filetype" name="!binary" path=".*" rule="must" group="defaultGroup" filefilter="binaryFileTypePolicyFilter" desc=""/>
    <policyitem type="filetype" name="!archive" path=".*" rule="must" group="defaultGroup" filefilter="binaryFileTypePolicyFilter" desc=""/>
    <policyitem type="import" name="!badlib" path="!.*/abc/.*" rule="must" group="defaultGroup" filefilter="defaultPolicyFilter" desc=""/>
</policy>
```

1、policy name：OAT框架根据该字段将各policy绑定对应的project，该字段必须与project的policy字段一致。

2、policyitem：每个policy可以包含多个policyitem，policyitem中可定义具体的审查规则，其中“rule”、”group“、”filefilter“字段是可选的，默认值如下：

```
<policyitem type="..." name="..." path="..." desc="..." rule="may" group="defaultGroup" filefilter="defaultPolicyFilter"/>
```

3、policyitem type：用于指定策略的类型，目前支持如下几种策略：

- "filetype" 用于检查文件类型，该规则会扫描所有文件的后缀以及文件二进制值，对于不满足该规则的会在报告中呈现，目前支持定义两种类型：archive、binary，即压缩文件以及二进制文件。
- "compatibility" 用于检查许可证兼容性，该规则会扫描所有源文件的许可声明信息，对于未声明许可证的会忽略，对于声明了许可证但是不满足该规则的会在报告中呈现。
- "license" 用于检查源代码许可头信息，该规则会扫描所有源文件的许可头信息，对于未声明或者声明的满足该规则的都会在报告中呈现。
- "copyright" 用于检查源代码版权头信息，该规则会扫描所有源文件的版权头信息，对于未声明或者声明的满足该规则的都会在报告中呈现。
- "filename" 用于检查指定目录是否存在指定文件，目前支持如下三种类型文件检测：LICENSE, README, README.OpenSource。
- "import" 用于检测依赖库是否合理，该规则会扫描所有源文件的依赖声明头信息，对于不满足规则的会在报告中呈现，目前支持两种依赖声明：import、include。

4、policyitem name: 用于定义规则的目标值，如许可证明等，其中"\*"表示允许所有值，"!"前缀则表示不允许该值。比如"!MIT"表示不允许使用MIT系列许可证。

5、policyitem path: 用于定义该规则应用的源文件范围，可以在此定义basedir路径以下的路径通配规则，其中"!"前缀表示该路径以外的文件，比如"!.\*/lib/.\*"表示lib以外的文件会应用该规则.

6、policyitem rule/group: 这两个字段要组合起来使用，用于计算多条规则应用后的综合结果，rule字段值为"may"表示在同一group中，只要有一个rule值为"may"的policyitem满足条件，结果即为检查通过；rule字段值为"must"
则如果该policyitem不满足，结果即为检查不通过。

7、policyitem filefilter: 用于绑定过滤器，如果扫描的文件符合过滤的条件，即使其不满足该policyitem的规则也不会在报告中呈现.

8、policyitem desc：用于描述该策略的原因，如desc字段要描述为什么要用此许可证，被什么特性使用，是否是跨进程通信等。

- 过滤配置

过滤器有两种使用场景：被project绑定以及被policyitem绑定，应用于前者时其作用是符合过滤规则的文件不再扫描，应用于后者时其作用是不论该policyitem是否满足，都不应用该policyitem，也即不会因该规则不满足而在报告中呈现。

过滤器的配置如下所示：

```
<filefilter name="copyrightPolicyFilter" desc="" >
    <filteritem type="filename" name="README|*.log|*.json" desc="..."/>
    <filteritem type="filepath" name="third_party_ltp/testcases/.*.bz2" desc="..."/>    
</filefilter>
```

1、filefilter name：OAT框架根据该字段将各过滤器绑定对应的project或policyitem，该字段必须与project或policyitem的filefilter字段一致。

2、filefilteritem type: 用于指定过滤类型，支持filename、filepath两种过滤类型。

3、filefilteritem name：用于定义过滤条件，对于filename类型的过滤，这里指定文件名通配符，支持以“|”分隔定义多种文件名；对于filepath类型的过滤，这里定义basedir路径以下的路径通配规则。

4、filefilteritem desc：用于说明要过滤该文件或文件夹的原因，比如有的数据文件不能写许可头注释等。

- 许可证匹配规则配置

OAT已默认支持OSI定义的大多数许可证类型的检测，若项目使用了某些特殊的许可证，或者是源码头的许可证描述添加了个性化信息导致OAT不能识别，您可以在此定义匹配规则：

```
<licensematcher name="XXX License" desc="License for XXX" >
    <licensetext name="
    license line 1
    license line 2
    license line 3
    ...
    end
     " desc=""/>
</licensematcher>
```

1、licensematcher name：用于指定其下licensetext对应的许可证名称。

2、licensetext name: 用于指定许可证文本，这里可以换行，但注意对于特殊的字符需要进行转义，转义规则如下：

```
" == &quot;
& == &amp;
' == &apos;
< == &lt;
> == &gt;
```

## 开源仓规则配置<a name="section119891146124010"></a>

开源仓规则即各开源仓下的OAT.xml中的规则配置，您可拷贝template/OAT.xml到您的开源仓根目录并定制该仓的扫描规则，该配置与默认规则配置基本一致，主要区别在于如下几点：

1、开源仓规则本身就是针对该具体的仓，因此该配置中不能再定义task以及project。

2、开源仓规则配置中支持定义许可证文件路径，对于三方开源仓因其许可证文件命名规范不完全一致，在此可重新指定：

```
<licensefile>/COPYRIGHT</licensefile>
```

3、开源仓规则配置中路径通配规则不用包含开源仓名本身，只需配置本开源仓根目录以下的路径通配规则：

```
<policy name="defaultPolicy" desc="" >
    <policyitem type="compatibility" name="Apache" path="dir name underproject/.*" rule="may" group="defaultGroup" filefilter="defaultPolicyFilter" desc=""/>
</policy>
...
<filefilter name="defaultPolicyFilter" desc="Files not to check">
    <filteritem type="filename" name="*.lds|*.pod"/>
    <filteritem type="filepath" name="dir name underproject/.*" desc="Describe the reason for filtering scan results"/>	
</filefilter>
```

## OAT问题确认<a name="section136713102427"></a>

如开源仓代码不满足OAT默认规则，则会在OAT扫描报告中呈现出相应的问题，请确认是否是问题，如果是问题请修改，如果确认不是问题，请在开源仓根目录的OAT.xml中进行屏蔽。

注意：

OpenHarmony CI门禁的问题详情界面提供了忽略问题的操作入口，但OAT工具检查的问题请不要在Web界面忽略，而应当在仓的根目录OAT.xml文件中进行确认（配置Policy或Filter），这样能确保该仓的确认信息继承到下游产品使用场景，避免重复进行问题确认；同时OAT.xml中配置规则能批量应用于很多文件，问题确认效率会更高。

- 二进制文件（Invalid File Type）

OpenHarmony社区开源仓中不应当存放过多二进制文件，如因业务需要必须存放二进制文件，请按如下步骤进行处理：

1、确认该二进制文件的版本是否为自研，如是自研跳到步骤3，如包含三方版权文件请执行步骤2。

2、如包含三方版权文件，请确认是否有正确履行其义务，即按照三方许可证条款提供NOTICE文件声明使用的软件名、许可证及版权信息。

3、确认该二进制文件的许可证信息，如果二进制文件对应的源码都在本仓中已开源，可以直接使用该仓的许可证；如果二进制文件对应的源码有部分不是开源的，则必须单独提供LICENSE，具体的LICENSE条款请与律师确认。

4、上述步骤处理完成后，请在binaryFileTypePolicyFilter中配置该二进制文件的过滤规则，并且在desc字段中详细描述二进制文件存在的理由以及上述处理的结果。

- 许可证不兼容（License Not Compatible）

如果开源仓用到了默认规则（Apache，BSD，MIT）以外的许可证，经与律师确认该许可证可以使用，或者使用场景满足该许可证的要求，则可通过添加policyitem的方式指定该许可证：

```
<policyitem type="compatibility" name="XXX-2.0+" path="abc/.*" rule="may" group="defaultGroup" filefilter="defaultPolicyFilter" desc="reason"/>
```

注意：

1、policyitem的name字段应该与扫描报告中的对应，如扫描报告显示“License Not Compatible XXX-2.0+”，则name字段应该就写“XXX-2.0+”，并在desc字段中描述具体的原因，如是跨进程调用等。

2、如果扫描报告检测的许可证名字为“InvalidLicense”，请将许可证映射到具体的许可证名字。

```
<licensematcher name="XXX License" desc="License for XXX" >
    <licensetext name="
    license line 1
    license line 2
    license line 3
    ...
    end
     " desc=""/>
</licensematcher>
```

3、policyitem的path尽可能限定到最小范围，避免将该规则泛化到更广的范围导致问题被掩盖。

- 源代码许可头缺失或不兼容（License Header Invalid）

跟上述许可证兼容性问题类似，如要新增许可证类型可通过如下方式配置：

```
<policyitem type="license" name="BSD" path="abc/.*" rule="may" group="defaultGroup" filefilter="defaultPolicyFilter" desc=""/>
```

如果本项目不需要该类型检测，如上游开源软件不需检测文件头是否遗漏许可声明，可通过如下方式配置：

```
<policyitem type="license" name="*" path=".*" rule="may" group="defaultGroup" filefilter="defaultPolicyFilter" desc=""/>
```

如若某些文件无法添加许可头，则可以在defaultPolicyFilter中配置过滤规则，并且在desc字段中详细说明过滤的理由。

- 源代码版权头缺失或错误（Copyright Header Invalid）

如需添加新的版权所有者，可通过如下方式配置：

```
<policyitem type="copyright" name="Copyright Owner" path="efg/.*" rule="may" group="defaultGroup" filefilter="copyrightPolicyFilter" desc=""/>
```

注意：完整的版权头格式为：Copyright (C) [第一次发布年份]-[当前版本发布年份] [版权所有者]
上述policyitem中name字段Copyright Owner不用包括Copyright (C) [第一次发布年份]-[当前版本发布年份]部分，只需配置[版权所有者]部分即可。
如果本项目不需要该类型检测，如上游开源软件不需检测文件头是否遗漏版权声明，可通过如下方式配置：

```
<policyitem type="copyright" name="*" path=".*" rule="may" group="defaultGroup" filefilter="copyrightPolicyFilter" desc=""/>
```

如若某些文件无法添加版权头，则可以在copyrightPolicyFilter中配置过滤规则，并且在desc字段中详细说明过滤的理由。

- 项目许可证文件缺失（No License File）

OpenHarmony项目要求许可证统一位于项目根目录，并且命名为“LICENSE”，如若是三方开源仓许可证不满足该要求，可以在licensefile字段处配置具体的LICENSE文件路径，OAT会根据此配置重新审查。

- 义务履行配置文件缺失（No README.OpenSource）

OpenHarmony项目要求所有第三方开源软件需要在其根目录提供README.OpenSource文件，用于在发布二进制版本时自动生成开源声明，该文件的具体写法参见[第三方开源软件引入指导](https://gitee.com/openharmony/docs/blob/master/zh-cn/contribute/%E7%AC%AC%E4%B8%89%E6%96%B9%E5%BC%80%E6%BA%90%E8%BD%AF%E4%BB%B6%E5%BC%95%E5%85%A5%E6%8C%87%E5%AF%BC.md)
。

- README文件缺失（No README）

OpenHarmony项目要求所有开源仓根目录提供README，README_zh文件，如缺失请补充。

- 其它

除上述几类问题外，其它问题都应当要修改开源仓代码直到满足OAT默认规则。

## Committer审核代码<a name="section77489114429"></a>

Committer在审核合入代码PR时须检查OAT报告以及各开源仓规则文件OAT.xml，每次批准代码合入时应当做到OAT检查结果清零，同时确保OAT.xml中的policy规则以及filefilter规则是合理的，对于无法确定的问题，可联系PMC或[基金会律师](law@openatom.md)
一同评审。

## 许可证<a name="section126611612164217"></a>

本项目遵从Apache License Version 2.0许可证。

[![OpenHarmony-SIG/tools_oat](https://gitee.com/openharmony-sig/tools_oat/widgets/widget_card.svg?colors=4183c4,ffffff,ffffff,e3e9ed,666666,9b9b9b)](https://gitee.com/openharmony-sig/tools_oat)