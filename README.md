#route

## 配置本地git，克隆仓库代码 ##
建议使用SSH的方式推送下载代码到Coding.net上，教程参照[https://coding.net/help/doc/git/ssh-key.html](https://coding.net/help/doc/git/ssh-key.html "配置SSH公钥")，设置账户SSH公钥

1. 在本地安装git
2. 在本地生成SSH公钥，-C之后的邮箱为Coding.net账号，生成的公钥在某个文件里，这个文件名会显示在终端上（# Your public key has been saved in ...）
`$ssh-keygen -t rsa -b 4096 -C "your_email@example.com"`
3. 在Coding.net网页上[https://coding.net/user/account/setting/keys](https://coding.net/user/account/setting/keys "SSH公钥")，添加刚才生成的SSH公钥
4. 克隆我们项目route的仓库到本地，SSH地址git@git.coding.net:huqifan/route.git

### 注意 ###
1. 本地提交代码不需要联网，把代码托管到网上需要联网
2. 如果大神们平时用github，那么coding.net的账号名尽量取和coding.net上一样的名字，不然以后再用github的话需要换账号名
3. 大家尽量不要同时修改同一个文件，否则提交的时候会有冲突，解决起来很麻烦。可以先下载新的版本，修改后马上提交，第二个人再下载，再修改。
4. 我不是很会用分支，没有研究过分支合并的时候是怎么解决冲突的，大家会可以教教我，我基本不用分支
5. 如果在windows系统中出现LF和CRLF等换行符问题，参考[http://www.tuicool.com/articles/IJjQVb](http://www.tuicool.com/articles/IJjQVb "CRLF和LF")，或者如下设置忽略换行符
`git config --global core.autocrlf false`

