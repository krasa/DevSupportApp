Web application with following features:
- finding not merged revision in SVN, diff, one click merge or merge of merginfo only
- tokenization across svn projects - replacing text(versions and stuff)
- finding not tagged commits
- custom build tool (connects via ssh and sends commands)
- IntelliJ enterprise plugin repository


This application contains logic and features which are specific to my work environment, it won't probably work right out of box for anyone, although I tried to introduce configurable strategies on some places. 
Take it more like inspiration, than something you could use as end-product. It was written in my limited free time to do what was needed to automate things and nothing more, so the code is most likely shit.


Technologies:
- Wicket, websockets
- Spring, Hibernate, SvnKit, HSQLDB
- Spring Boot, Jetty

![Finding merges](https://raw.github.com/krasa/SVNMergeInfo/master/screenshot.png)


![Building](https://raw.github.com/krasa/SVNMergeInfo/master/screenshotBuild.png)
