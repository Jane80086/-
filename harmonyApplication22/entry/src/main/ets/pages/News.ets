import { AppStorageV2 } from "@kit.ArkUI"
import { BusinessError } from '@ohos.base';

import http from '@ohos.net.http';

import{ NewsList} from "./NewsList";
// interface NewsClass {
//   title: string
//   text: string
//   icon: ResourceStr
// }

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

// 转换后的新闻类，用于UI展示 - 修改icon类型为联合类型
interface NewsClass {
  id: number;
  title: string;
  text: string;
  icon: ResourceStr | string; // 允许字符串或ResourceStr类型
  createTime: string;
  viewCount: number; // 添加viewCount属性
}




@Component
export struct News {
  // 跳转
  @State pathStack: NavPathStack = AppStorageV2.connect(NavPathStack, 'navStack', () => new NavPathStack())!

  @State newsList: NewsClass[] = [];
  @State refreshing: boolean = false;
  @State loading: boolean = false;
  @State page: number = 1;
  @State hasMore: boolean = true;
  @State total: number = 0;

  aboutToAppear() {
    this.loadNews();
  }


  // 将后端数据转换为UI数据格式 - 使用显式类型断言
  convertToNewsClass(items: NewsItem[]): NewsClass[] {
    return items.map(item => {
      const newsItem: NewsClass = {
        id: item.id,
        title: item.title,
        text: item.summary || item.content,
        icon: item.image ? item.image : $r('app.media.meeting1'),
        createTime: item.formattedCreateTime,
        viewCount: item.viewCount
      };
      return newsItem;
    });
  }

  // 从后端获取新闻数据
  async loadNews(isRefresh: boolean = false) {
    if (this.refreshing || this.loading) return;

    if (isRefresh) {
      this.page = 1;
      this.hasMore = true;
      this.refreshing = true;
    } else {
      this.loading = true;
    }

    try {
      // 创建HTTP请求
      const httpRequest = http.createHttp();
      const response = await httpRequest.request(
        'http://localhost:8083/api/news/search' + this.page,
        {
          method: http.RequestMethod.GET,
          readTimeout: 60000,
          header: { 'Content-Type': 'application/json' }
        }
      );


      if (response.responseCode === 200) {
        const resultText = response.result.toString();
        const responseData = JSON.parse(resultText) as ResponseData;

        if (responseData.code === 1) {
          const newsItems = responseData.data.list;
          const convertedData = this.convertToNewsClass(newsItems);

          if (isRefresh) {
            this.newsList = convertedData;
          } else {
            this.newsList = [...this.newsList, ...convertedData];
          }

          this.total = responseData.data.total;
          this.hasMore = this.newsList.length < this.total;

          if (this.hasMore) {
            this.page++;
          }
        } else {
          console.error('获取新闻失败:', responseData.msg);
        }
      } else {
        console.error('HTTP请求失败，状态码:', response.responseCode);
      }
    } catch (error) {
      console.error('网络请求异常:', (error as BusinessError).message);
    } finally {
      this.refreshing = false;
      this.loading = false;
    }
  }

  // 处理下拉刷新
  onRefresh() {
    this.loadNews(true);
  }

  build() {
    Column() {
      // 搜索框
      Row() {
        Image($r('app.media.search'))
          .width('20')
          .fillColor(Color.Gray)
        TextInput({ placeholder: '请输入搜索关键词' })
          .placeholderColor(Color.Gray)
          .backgroundColor('#FFF')
          .layoutWeight(1)
          .fontColor(Color.Black)
      }
      .width('100%')
      .height('40')
      .padding({ left: 9, right: 9 })
      .backgroundColor('#FFF')
      .border({ radius: 20 })
      .margin({ bottom: 8 })

      // 下拉刷新组件
      Refresh({
        refreshing: this.refreshing,
        // onRefresh: this.onRefresh.bind(this)
      }) {
        // 使用子组件展示新闻列表
        NewsList({
          newsList:this.newsList,
          refreshing: $refreshing,
          loading: $loading,
          hasMore: $hasMore,
          pathStack: $pathStack,
          // onLoadMore: this.loadNews.bind(this)
        })
      }
    }
    .width('100%')
    .height('100%')
    .padding({ left: 10, right: 10, top: 5, bottom: 5 })
    .backgroundColor('#F5F5F5')
  }
}