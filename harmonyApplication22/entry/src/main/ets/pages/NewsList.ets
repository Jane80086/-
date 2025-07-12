import { AppStorageV2 } from "@kit.ArkUI"
import http from '@ohos.net.http';
import { BusinessError } from '@ohos.base';

// 后端返回的完整数据结构
interface GeneratedTypeLiteralInterface_1 {
  list: NewsItem[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
  empty: boolean;
}

interface ResponseData {
  code: number;
  msg: string;
  data: GeneratedTypeLiteralInterface_1;
}

// 新闻项数据结构
interface NewsItem {
  id: number;
  title: string;
  image: string;
  content: string;
  summary: string;
  author: string;
  createTime: string;
  status: number;
  statusText: string;
  viewCount: number;
  canEdit: boolean;
  canDelete: boolean;
  isOwner: boolean;
  auditComment: string | null;
  auditTime: string | null;
  formattedCreateTime: string;
  statusDisplayName: string;
}

// 转换后的新闻类，用于UI展示
interface NewsClass {
  id: number;
  title: string;
  text: string;
  icon: ResourceStr | string;
  createTime: string;
}

@Component
export struct NewsList {
  @Link newsList: NewsClass[];
  @Link refreshing: boolean;
  @Link loading: boolean;
  @Link hasMore: boolean;
  @Link pathStack: NavPathStack;
  // 本地状态

  private itemsPerPage: number = 5;  // 每页显示数量
//   hasMoree: boolean=true
//   loadingg:boolean= true
//   newsListt: NewsClass[]=[
//     {id:1,icon:$r('app.media.meeting1'),title:'new1',text:'new1111',createTime:'20250613'},
//     {id:2,icon:$r('app.media.meeting2'),title:'new2',text:'new1111',createTime:'20250613'},
//     {id:3,icon:$r('app.media.meeting3'),title:'new3',text:'new1111',createTime:'20250613'},
//     {id:4,icon:$r('app.media.meeting1'),title:'new4',text:'new1111',createTime:'20250613'},
//     {id:5,icon:$r('app.media.meeting2'),title:'new5',text:'new1111',createTime:'20250613'},
//     {id:6,icon:$r('app.media.meeting3'),title:'new6',text:'new1111',createTime:'20250613'},
//     {id:7,icon:$r('app.media.meeting1'),title:'new7',text:'new1111',createTime:'20250613'},
//     {id:8,icon:$r('app.media.meeting2'),title:'new8',text:'new1111',createTime:'20250613'},
//     {id:9,icon:$r('app.media.meeting3'),title:'new9',text:'new1111',createTime:'20250613'},
//     {id:10,icon:$r('app.media.meeting1'),title:'new10',text:'new1111',createTime:'20250613'},
//     {id:11,icon:$r('app.media.meeting2'),title:'new11',text:'new1111',createTime:'20250613'},
//     {id:12,icon:$r('app.media.meeting3'),title:'new12',text:'new1111',createTime:'20250613'},
// ]

  // 当前显示的项目索引范围
 @State startIndex: number = 0;
  @State endIndex: number = this.itemsPerPage;

  onLoadMore: () => void = () => {
    setTimeout(()=>{
      console.log('调用了这个函数')
      this.loading=true
      if (this.loading || !this.hasMore) return;

      // 更新索引以显示更多项目
      // this.startIndex = this.endIndex;
      this.endIndex = Math.min(this.endIndex + this.itemsPerPage, this.newsList.length);

      // 如果没有更多项目可加载，更新hasMore状态
      if (this.endIndex >= this.newsList.length) {
        console.log('执行了这个判断')
        this.hasMore = false;
      }
      this.loading=false
    },2000)

  }

  build() {
    List() {
      // 只渲染当前索引范围内的项目
      ForEach(this.newsList.slice(this.startIndex, this.endIndex), (item: NewsClass) => {
        ListItem() {
          Row({ space: 6 }) {
            // 如果有图片URL则使用网络图片，否则使用默认图标
            if (item.icon) {
              Image(item.icon as string)
                .width(120)
                .height(120)
                .objectFit(ImageFit.Cover)
            } else {
              Image($r('app.media.meeting1'))
                .width(120)
                .height(120)
            }

            Column() {
              Text(item.title)
                .fontSize(16)
                .fontWeight(FontWeight.Medium)
                .maxLines(2)
                .textOverflow({ overflow: TextOverflow.Ellipsis })
                .width('100%')
                .margin({ bottom: 4 })

              Text(item.text)
                .fontSize(14)
                .maxLines(2)
                .textOverflow({ overflow: TextOverflow.Ellipsis })
                .width('100%')
                .fontColor('#666666')
                .margin({ bottom: 4 })

              Row() {
                Text(item.createTime)
                  .fontSize(12)
                  .fontColor('#999999')
              }
              .width('100%')
            }
            .layoutWeight(1)
            .justifyContent(FlexAlign.Start)
          }
          .width('95%')
          .height(140)
          .backgroundColor(Color.White)
          .margin({ top: 5, bottom: 5 })
          .padding(10)
          .borderRadius(8)
        }
        .onClick(() => {
          this.pathStack.pushPathByName('Detail',  item.id , false)
        })
      })

      // 加载更多指示器
      if (this.loading || this.hasMore) {
        ListItem() {
          Row() {
            if (this.loading) {
              LoadingProgress()
                .color('#007DFF')
                .width(24)
                .height(24)
                .margin({ right: 10 })
            }

            Text(this.loading ? '加载中...' : '上拉加载更多')
              .fontSize(14)
              .fontColor('#666666')
          }
          .width('100%')
          .height(60)
          .justifyContent(FlexAlign.Center)
        }
      }
    }
    .width('100%')
    .layoutWeight(1)
    .scrollBar(BarState.Off)
    .onReachEnd(() => this.onLoadMore())
  }
}







