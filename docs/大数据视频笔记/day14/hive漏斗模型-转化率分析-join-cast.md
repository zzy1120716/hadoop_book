## 一、根据随机生成的数据进行漏斗模型分析
1. 创建临时表
```
create table tmp_page_views
like
ods_weblog_origin;
```

2. 导入数据
```
load data local inpath '/home/hadoop/hivedata/mylog.log'
into table tmp_page_views partition(datestr='2013-09-18');
```

3. 查询每一个步骤的总访问人数
```
create table dw_route_numbs as 
select 'step1' as step,count(distinct remote_addr)  as numbs from tmp_page_views where request like '/item%'
union
select 'step2' as step,count(distinct remote_addr) as numbs from tmp_page_views where request like '/category%'
union
select 'step3' as step,count(distinct remote_addr) as numbs from tmp_page_views where request like '/order%'
union
select 'step4' as step,count(distinct remote_addr)  as numbs from tmp_page_views where request like '/index%';
```

4. 查询每一步骤相对于路径起点人数的比例
子查询--内连接部分：
```
select rn.step as rnstep,rn.numbs as rnnumbs,rr.step as rrstep,rr.numbs as rrnumbs  from dw_route_numbs rn
inner join 
dw_route_numbs rr;
```
全部查询：
```
select tmp.rnstep,tmp.rnnumbs/tmp.rrnumbs as ratio
from
    (
        select rn.step as rnstep,rn.numbs as rnnumbs,rr.step as rrstep,rr.numbs as rrnumbs  from dw_route_numbs rn
        inner join 
        dw_route_numbs rr
    ) tmp
where tmp.rrstep='step1';
```

5. 查询每一步骤相对于上一步骤的漏出率(cast做类型转换)
```
select tmp.rrstep as rrstep,tmp.rrnumbs/tmp.rnnumbs as ration
from
    (
        select rn.step as rnstep,rn.numbs as rnnumbs,rr.step as rrstep,rr.numbs as rrnumbs  from dw_route_numbs rn
        inner join 
        dw_route_numbs rr
    ) tmp
where cast(substr(tmp.rnstep,5,1) as int)=cast(substr(tmp.rrstep,5,1) as int)-1;
```

6. 汇总以上两种指标
```
select abs.step,abs.numbs,abs.ratio as abs_ratio,rel.ratio as rel_ratio
from 
    (
        select tmp.rnstep as step,tmp.rnnumbs as numbs,tmp.rnnumbs/tmp.rrnumbs as ratio
        from
        (
            select rn.step as rnstep,rn.numbs as rnnumbs,rr.step as rrstep,rr.numbs as rrnumbs  from dw_route_numbs rn
            inner join 
            dw_route_numbs rr
        ) tmp
        where tmp.rrstep='step1'
    ) abs
left outer join
    (
        select tmp.rrstep as step,tmp.rrnumbs/tmp.rnnumbs as ratio
        from
        (
            select rn.step as rnstep,rn.numbs as rnnumbs,rr.step as rrstep,rr.numbs as rrnumbs  from dw_route_numbs rn
            inner join 
            dw_route_numbs rr
        ) tmp
        where cast(substr(tmp.rnstep,5,1) as int)=cast(substr(tmp.rrstep,5,1) as int)-1
    ) rel
on abs.step=rel.step;
```

## 二、简单数据示例
数据loudou.dat:
```
step1,10000
step2,4000
step3,2000
step4,1000
step5,500
step6,200
```

1. 创建初始表
```
create table t_route_numbs(step string,numbs string)
row format delimited fields terminated by ',';
```

2. 导入数据
```
load data local inpath '/home/hadoop/hivedata/loudou.dat'
into table t_route_numbs;
```

3. 查询每一步骤相对于路径起点人数的比例
自己join自己的子查询：
```
select l.step as l_step,l.numbs as l_numbs,r.step as r_step,r.numbs as r_numbs
from t_route_numbs l inner join t_route_numbs r;
```
绝对转化率：
```
select tmp.l_step,tmp.l_numbs/tmp.r_numbs as ratio
from
    (
        select l.step as l_step,l.numbs as l_numbs,r.step as r_step,r.numbs as r_numbs
        from t_route_numbs l inner join t_route_numbs r
    ) tmp
where tmp.r_step='step1';
```

4. 查询每一步骤相对于上一步骤的漏出率
```
select tmp.r_step as r_step,tmp.r_numbs/tmp.l_numbs as ratio2
from
    (
        select l.step as l_step,l.numbs as l_numbs,r.step as r_step,r.numbs as r_numbs
        from t_route_numbs l inner join t_route_numbs r
    ) tmp
where cast(substr(tmp.l_step,5,1) as int)=cast(substr(r_step,5,1) as int)-1;
```

5. 汇总到一张表
```
select abs.l_step as step,abs.l_numbs as numbs,abs.ratio as abs_ratio,rel.ratio as rel_ratio
from
    (
        select tmp.l_step,tmp.l_numbs,tmp.l_numbs/tmp.r_numbs as ratio
        from
            (
                select l.step as l_step,l.numbs as l_numbs,r.step as r_step,r.numbs as r_numbs
                from t_route_numbs l inner join t_route_numbs r
            ) tmp
        where tmp.r_step='step1'
    ) abs
left outer join
    (
        select tmp.r_step as r_step,tmp.r_numbs/tmp.l_numbs as ratio
        from
            (
                select l.step as l_step,l.numbs as l_numbs,r.step as r_step,r.numbs as r_numbs
                from t_route_numbs l inner join t_route_numbs r
            ) tmp
        where cast(substr(tmp.l_step,5,1) as int)=cast(substr(r_step,5,1) as int)-1
    ) rel
on abs.l_step=rel.r_step;
```

```
+--------+--------+------------+------------+--+
|  step  | numbs  | abs_ratio  | rel_ratio  |
+--------+--------+------------+------------+--+
| step1  | 10000  | 1.0        | NULL       |
| step2  | 4000   | 0.4        | 0.4        |
| step3  | 2000   | 0.2        | 0.5        |
| step4  | 1000   | 0.1        | 0.5        |
| step5  | 500    | 0.05       | 0.5        |
| step6  | 200    | 0.02       | 0.4        |
+--------+--------+------------+------------+--+
```