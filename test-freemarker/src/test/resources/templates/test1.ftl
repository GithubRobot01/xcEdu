<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Hello World!</title>
</head>
<body>
Hello ${name}!
<br>
遍历数据模型中的list学生信息
<table>
    <tr>
        <th>序号</th>
        <th>姓名</th>
        <th>年龄</th>
        <th>金额</th>
        <th>出生日期</th>
    </tr>
    <#if stus??>
        <#list stus as stu>
        <tr>
            <td>${stu_index+1}</td>
            <td <#if stu.name!="小明">style="background:red"</#if>>${stu.name}</td>
            <td>${stu.age}</td>
            <td <#if stu.money gt 300>style="background:yellow"</#if>>${stu.money}</td>
            <#--<td>${stu.birthday?date}</td>-->
            <td>${stu.birthday?string("yyyy年MM月dd日")}</td>
        </tr>
        </#list>
    </#if>

</table>
<br>
遍历数据模型中的stuMap<br>
姓名:${(stuMap['stu1'].name)!''} 年龄:${(stuMap['stu1'].age)!''}<br>
姓名:${stuMap['stu2'].name} 年龄:${stuMap['stu2'].age}<br>
姓名:${(stuMap.stu1.name)!''} 年龄:${(stuMap.stu1.age)!''}<br>
姓名:${stuMap.stu2.name} 年龄:${stuMap.stu2.age}<br>
-----<br>
<#list stuMap?keys as k>
    姓名:${stuMap[k].name} 年龄:${stuMap[k].age}<br>
</#list>
<br>
${point?c}
<br>
<#assign text="{'bank':'建行','account':'123456'}">
<#assign data=text?eval/>
银行:${data.bank} 账户:${data.account}
</body>
</html>