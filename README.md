# OSS Audit Tool<a name="EN-US_TOPIC_0000001106844024"></a>

- [Introduction](#section11660541593)
- [Features](#section367519246426)
- [Directory Structure](#section161941989596)
- [Constraints](#section119744591305)
- [Build](#section137768191623)
- [CI Integration](#section7899558173218)
- [Running](#section1634911263317)
    - [Multi-Project Scan](#section20292217143516)
    - [Single-Project Scan](#section1771013213818)

- [Default Rule Configuration](#section729883153314)
- [Open-Source Repository Rule Configuration](#section119891146124010)
- [OAT Issue Confirmation](#section136713102427)
- [Code Review by the Committer](#section77489114429)
- [License](#section126611612164217)

## Introduction<a name="section11660541593"></a>

OSS Audit Tool \(OAT\) is an automated review tool for the OpenHarmony community. It can automatically scan the code in open-source repositories based on custom rules, identify the code that does not
comply with the rules, and generate a scan report.

OAT does not provide a complex GUI or require extra database deployment or maintenance. It does not involve any complex operation processes, bringing no adverse impact on your main service process.
Instead, it provides flexible source code scanning methods, highly customizable license matching and auditing rules, simplified data formats for reports, and flexible issue filtering and confirmation
rules. You can deploy this tool easily, configure scan rules, start a scan, confirm issues, and archive the issue confirmation result in the repository to facilitate follow-up scans.

## Features<a name="section367519246426"></a>

OAT provides the scalability you need to extend the logic for code scanning and issue reporting. Based on the development specifications of the OpenHarmony community, OAT supports scanning of the
following types of files by default:

- Binary files
- License compatibility
- Source code license headers
- Source code copyright headers
- Project license files
- Project README files
- Project README.OpenSource files
- Include/Import dependencies

All the preceding types support custom scan rules, such as scanning the code in a specified path for the licenses and copyright headers used. OAT applies both default global rules and project-specific
rules to scan license files and copyright information of different open-source repositories, and license changes during the evolution of the same open-source repository.

## Directory Structure<a name="section161941989596"></a>

```
/tools/oat
├── src         # OAT code
├── template    # Configuration file template for OAT
```

## Constraints<a name="section119744591305"></a>

OAT is developed using Java and depends on Java 8 or a later version.

## Build<a name="section137768191623"></a>

OAT is built based on Maven 3.5.2. Before you start, ensure that Maven has been installed in your environment. Then run the following command to build OAT:

```
mvn package
```

After the preceding command is executed, the  **ohos\_ossaudittool-xx.jar**  file is generated in the  **target**  directory, and the dependency libraries are copied to the  **target/libs**
directory. You need to copy the  **ohos\_ossaudittool-xx.jar**  file as well as the  **target/libs**  directory to the environment where you want to run OAT, and keep the dependency libraries of OAT
in the  **libs**  directory of your environment.

Note: During the build, you need to download the OAT dependency libraries from the remote Maven repository. Ensure that you can access the remote Maven repository whose address is specified in the  **
settings.xml**  file of Maven.

## CI Integration<a name="section7899558173218"></a>

Open-source projects in the OpenHarmony community use multiple programming languages and build tools. OAT only scans the source code text of open-source repositories, regardless of what languages they
use. Therefore, OAT is not directly integrated into the build logic of open-source repositories. Instead, it is integrated into the code check tools for the OpenHarmony community as a common tool.
After the code of each open-source repository is submitted, an OAT scan is automatically triggered and a report is generated. If you want to integrate OAT into the project build process, refer
to  [Single-Project Scan](#section1771013213818).

## Running<a name="section1634911263317"></a>

OAT is developed based on the Java language and can run in multiple operating systems. Ensure that JDK 8 or a later version has been installed in your environment. OAT can scan a single project or
multiple projects in a batch.

### Multi-Project Scan<a name="section20292217143516"></a>

```
java -Dfile.encoding=UTF-8 -jar ohos_ossaudittool-xx.jar -i OAT-ALL.xml
```

All supported parameters are as follows:

```
usage: java -jar ohos_ossaudittool-VERSION.jar [options]

Available options
 -f <arg>   File list to check, separated by |, must be used together with
            -s option
 -h         Help message
 -i <arg>   OAT.xml file path, default vaule is OAT.xml in the running
            path
 -k         Trace skipped files and ignored files
 -l         Log switch, used to enable the logger
 -m <arg>   Check mode, 0 means full check, 1 means only check the file
            list, must be used together with -s option
 -n <arg>   Name of repository, used to match the default policy, must be
            used together with -s option
 -r <arg>   Report file path, must be used together with -s option
 -s <arg>   Source code repository path
 -t         Trace project license list only
```

In this mode, the report is generated in the running directory of OAT. The  **OAT-ALL.xml**  file is used to configure the list of projects to be scanned, the default license and copyright policy, and
the default filtering rule.

Configure the projects to be scanned and their paths as follows:

1. Configure an absolute path for the project-level repositories to be scanned in  **basedir**.

```
<basedir>/home/cyx/OpenHarmony/</basedir>
```

2. Configure scan tasks in  **tasklist**. Each task starts an independent thread for scanning and generates a report accordingly. For a repository with a large amount of code, you are advised to
   configure an independent task to improve the scanning efficiency.

```
<tasklist>
    <task name="defaultTask" policy="defaultPolicy" filefilter="defaultFilter" desc="">
        <project name="hmf/kernel/liteos-a" path="kernel/liteos_a"/>
    </task>
</tasklist>
```

3. Configure the name of an open-source repository in  **project name**, and in  **path**  configure the path of the repository in the  **basedir**  directory. Ensure that  **basedir**  and  **
   projectpath**  together form the root directory of the project.

4. You can also configure the  **policy**  and  **filefilter**  attributes for the project. By default, the  **policy**  and  **filefilter**  attribute values of the corresponding task are used.

5. You can configure rules such as licenses and copyright headers in  **policy**  and configure the files not to be scanned in  **filefilter**. For details,
   see  [Default Rule Configuration](#section729883153314).

6. If the default rules do not meet your requirements, you can place an  **OAT.xml**  file in the root directory of each open-source repository to customize the rules. For details,
   see  [Open-Source Repository Rule Configuration](#section119891146124010).

### Single-Project Scan<a name="section1771013213818"></a>

```
java -Dfile.encoding=UTF-8 -jar ohos_ossaudittool-xx.jar -s sourcedir -r reportdir -n nameOfRepo
```

**sourcedir**  indicates the root directory of the project to be scanned, and  **reportdir**  indicates the directory used to store the generated report. In this mode, the rules defined in  **
resources/OAT-Default.xml**  are used by default. If the default rules do not meet your requirements, you can place an  **OAT.xml**  file in the root directory of the open-source repository to
customize the rules. For details, see  [Open-Source Repository Rule Configuration](#section119891146124010).

OAT has been integrated into the code check tools for the OpenHarmony community. When access control is triggered, OAT runs in this mode. You can view the default rules of OAT in  **
resources/OAT-Default.xml**.

## Default Rule Configuration<a name="section729883153314"></a>

Default rule configurations include configuring policies \(**policylist**\), filters \(**filefilterlist**\), and license matching rules \(**licensematcherlist**\).

- Configuring policies

The policies define how OAT audits code. Each project is associated with a specific policy. The following is an example policy:

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

1. **policy name**: OAT uses this field to associate the policy with a project. This field must be consistent with the  **policy**  field of the project.

2. **policyitem**: A policy item defines specific audit rules. Each policy can contain multiple policy items. The  **rule**,  **group**, and  **filefilter**  fields are optional. The default value is
   as follows:

```
<policyitem type="..." name="..." path="..." desc="..." rule="may" group="defaultGroup" filefilter="defaultPolicyFilter"/>
```

3. **policyitem type**: specifies the type of the policy. The following policy types are supported:

- **filetype**: checks the file type. This type of policies scans the name extensions and binary values of all files. The files that do not meet requirements are displayed in the report. Currently,
  two file types are supported,  **archive**  and  **binary**, representing compressed files and binary files, respectively.
- **compatibility**: checks the license compatibility. This type of policies scans the license declaration information of all source files. Undeclared licenses are ignored, and declared licenses that
  do not meet requirements are displayed in the report.
- **license**: checks the license header information of the source code. This type of policies scans the license headers of all source files. Both the undeclared license headers and the declared
  license headers that meet requirements are displayed in the report.
- **copyright**: checks the copyright header information of the source code. This type of policies scans the copyright headers of all source files. Both the undeclared copyright headers and the
  declared copyright headers that meet requirements are displayed in the report.
- **filename**: checks whether a specified directory contains a given file. Currently, the following three types of files can be checked: LICENSE, README, and README.OpenSource.
- **import**: checks whether a dependency library is reasonable. This type of policies scans the dependency declaration headers of all source files and displays those that do not meet requirements in
  the report. Currently, two types of dependency declarations can be checked:  **import**  and  **include**.

4. **policyitem name**: specifies the target of the policy, for example, license. The asterisk \(\*\) indicates that the target is allowed, and the exclamation mark \(!\) indicates that the target is
   not allowed. For example,  **!GPL**  indicates that GPL licenses cannot be used.

5. **policyitem path**: specifies the source files to which the policy applies. You can define a policy for all the paths in  **basedir**. The exclamation mark \(!\) prefix indicates an exclusion. For
   example,  **!.\*/lib/.\***  indicates that the policy applies to all files except the  **lib**  file.

6. **policyitem rule**/**group**: The two fields are used together to determine the result after multiple policies are applied. If the value of the  **rule**  field is  **may**, it indicates that the
   audit is successful as long as one  **policyitem**  is met. If the value of the  **rule**  field is  **must**, it indicates that the audit fails as long as the  **policyitem**  is not met.

7. **policyitem filefilter**: specifies the file filter. A file that meets the filtering condition will not be displayed in the report even if the file does not meet the  **policyitem**.

8. **policyitem desc**: specifies the reason for using the policy. For example, if GPLv2 is used, this field describes why and by what features it is used, as well as whether cross-process
   communication is used.

- Configuring filters

A filter can be associated with a project or a policy item. When it is associated with a project, files that meet the filtering rule will not be scanned. When it is associated with a policy item,
files will not be displayed in the report even if they do not meet the policy item.

An example filter is as follows:

```

<filefilter name="copyrightPolicyFilter" desc="" >
    <filteritem type="filename" name="README|*.log|*.json" desc="..."/>
    <filteritem type="filepath" name="third_party_ltp/testcases/.*.bz2" desc="..."/>    
</filefilter>

```

1. **filefilter name**: OAT uses this field to associate the filter with a project or policy item. This field must be consistent with the  **filefilter**  field of the project or policy item.

2. **filefilteritem type**: specifies the filtering type. The value can be  **filename**  or  **filepath**.

3. **filefilteritem name**: specifies the filtering condition. If the filtering type is  **filename**, specify wildcard characters for file names and separate the names by vertical bars \(|\). If the
   filtering type is  **filepath**, specify a wildcard rule for the paths in  **basedir**.

4. **filefilteritem desc**: specifies the reason why a file or folder needs to be filtered. For example, some data files do not allow comments in the license header.

- Configuring license matching rules

By default, OAT can detect most license types defined by the OSI. If a project uses some special licenses or the license description of the source code contains personalized information, OAT may fail
to identify the licenses. In this case, you can define the matching rule as follows:

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

1. **licensematcher name**: specifies the name of the license corresponding to the license text.

2. **licensetext name**: specifies the license text. The license text can be displayed in a new line, but special characters must be escaped based on the following rules:

```
" == &quot;
& == &amp;
' == &apos;
< == &lt;
> == &gt;
```

## Open-Source Repository Rule Configuration<a name="section119891146124010"></a>

Rules for each open-source repository are stored in the  **OAT.xml**  file in the repository. You can copy the  **template/OAT.xml**  file to the root directory of your open-source repository and
customize the scan rules for the repository. The rules are basically the same as the default rules, except that:

1. Tasks or projects cannot be defined in the rules for an open-source repository.

2. License file paths can be re-defined in the rules for an open-source repository.

```
<licensefile>/COPYRIGHT</licensefile>
```

3. The wildcard path rules for an open-source repository do not contain the repository name. You only need to configure wildcard rules for the paths in the root directory of the repository.

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

## OAT Issue Confirmation<a name="section136713102427"></a>

If code in an open-source repository does not meet the default rules of OAT, an issue will be displayed in the OAT scan report. You need to take measures to handle this issue.

- Binary files

You should not store too many binary files in an open-source repository in the OpenHarmony community. If you must store a binary file, perform the following steps:

1. Check whether the binary file is developed by yourself. If so, go to step 3; if not \(the binary file contains a third-party copyright file\), go to step 2.

2. Check whether you have fulfilled the obligations defined in the third-party copyright file, for example, declaring the software name, license, and copyright information in a NOTICE file according
   to the third-party license terms.

3. Check the license information of the binary file. If the source code of the binary file is opened in the current repository, you can directly use the license of the repository. If part of the
   source code of the binary file is not opened, you must provide an independent license. For details about the license terms, contact a lawyer of the OpenAtom Foundation.

4. After the preceding steps are complete, configure the filtering rule of the binary file in  **binaryFileTypePolicyFilter**  and specify the reason for using the binary file and the handling result
   in the  **desc**  field.

- License compatibility

If an open-source repository uses a license that does not comply with the default rules, you can add a policy item to specify the license after confirming with a lawyer of the OpenAtom Foundation that
the license can be used.

```
<policyitem type="compatibility" name="BSD" path="abc/.*" rule="may" group="defaultGroup" filefilter="defaultPolicyFilter" desc=""/>
```

Keep  **path**  in the minimum range to prevent issues from being neglected.

- Source code license headers

Similar to license compatibility, you can add a license type as follows:

```
<policyitem type="license" name="BSD" path="abc/.*" rule="may" group="defaultGroup" filefilter="defaultPolicyFilter" desc=""/>
```

If this project do not need check license headers, such as third party software, you can configure policy as follows：

```
<policyitem type="license" name="*" path=".*" rule="may" group="defaultGroup" filefilter="defaultPolicyFilter" desc=""/>
```

If some files do not support license headers, you can configure filtering rules in  **defaultPolicyFilter**  and specify the filtering reason in the  **desc**  field.

- Source code copyright headers

You can add a copyright owner as follows:

```
<policyitem type="copyright" name="Copyright Owner" path="efg/.*" rule="may" group="defaultGroup" filefilter="copyrightPolicyFilter" desc=""/>
```

If this project do not need check copyright headers, such as third party software, you can configure policy as follows：

```
<policyitem type="copyright" name="*" path=".*" rule="may" group="defaultGroup" filefilter="copyrightPolicyFilter" desc=""/>
```

If some files do not support copyright headers, you can configure filtering rules in  **copyrightPolicyFilter**  and specify the filtering reason in the  **desc**  field.

- Project license files

The license file for the OpenHarmony project must be stored in the root directory and named  **LICENSE**. For a third-party open-source repository that does not meet this requirement, you can
configure the license file path in the  **licensefile**  field. OAT will audit the license file again based on the configuration.

- Others

For any other issue, you need to modify the code of your open-source repository until the default OAT rules are met.

## Code Review by the Committer<a name="section77489114429"></a>

When reviewing the code for each open-source repository, the committer must check the OAT report and the  **OAT.xml**  file. Before approving code merge, the committer must ensure that all issues have
been resolved and that the policy and  **filefilter**  rules in the  **OAT.xml**  file are reasonable. For the issues that cannot be resolved, the committer can contact the PMC
or  [a lawyer of the OpenAtom Foundation](law@openatom.md).

## License<a name="section126611612164217"></a>

This project complies with the Apache License Version 2.0.

