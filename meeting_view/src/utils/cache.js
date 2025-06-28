/**
 * 增强的缓存工具类
 * 支持内存缓存、本地存储缓存、过期时间、容量限制等功能
 */

class CacheManager {
  constructor(options = {}) {
    this.memoryCache = new Map();
    this.maxMemorySize = options.maxMemorySize || 100; // 最大内存缓存条目数
    this.maxLocalStorageSize = options.maxLocalStorageSize || 10 * 1024 * 1024; // 最大本地存储大小 (10MB)
    this.defaultTTL = options.defaultTTL || 5 * 60 * 1000; // 默认过期时间 5分钟
    this.enableLocalStorage = options.enableLocalStorage !== false;
    this.enableMemoryCache = options.enableMemoryCache !== false;
    
    // 清理过期缓存
    this.startCleanupInterval();
  }

  /**
   * 设置缓存
   * @param {string} key 缓存键
   * @param {any} value 缓存值
   * @param {Object} options 选项
   * @param {number} options.ttl 过期时间(毫秒)
   * @param {boolean} options.persistent 是否持久化到本地存储
   * @param {string} options.namespace 命名空间
   */
  set(key, value, options = {}) {
    const {
      ttl = this.defaultTTL,
      persistent = false,
      namespace = 'default'
    } = options;

    const cacheItem = {
      value,
      timestamp: Date.now(),
      ttl,
      namespace,
      expiresAt: Date.now() + ttl
    };

    const fullKey = this.getFullKey(key, namespace);

    try {
      // 内存缓存
      if (this.enableMemoryCache) {
        this.setMemoryCache(fullKey, cacheItem);
      }

      // 本地存储缓存
      if (persistent && this.enableLocalStorage) {
        this.setLocalStorageCache(fullKey, cacheItem);
      }

      return true;
    } catch (error) {
      console.error('设置缓存失败:', error);
      return false;
    }
  }

  /**
   * 获取缓存
   * @param {string} key 缓存键
   * @param {Object} options 选项
   * @param {string} options.namespace 命名空间
   * @param {any} options.defaultValue 默认值
   */
  get(key, options = {}) {
    const {
      namespace = 'default',
      defaultValue = null
    } = options;

    const fullKey = this.getFullKey(key, namespace);

    try {
      // 优先从内存缓存获取
      if (this.enableMemoryCache) {
        const memoryItem = this.getMemoryCache(fullKey);
        if (memoryItem && !this.isExpired(memoryItem)) {
          return memoryItem.value;
        }
      }

      // 从本地存储获取
      if (this.enableLocalStorage) {
        const localItem = this.getLocalStorageCache(fullKey);
        if (localItem && !this.isExpired(localItem)) {
          // 如果本地存储有数据但内存没有，则同步到内存
          if (this.enableMemoryCache) {
            this.setMemoryCache(fullKey, localItem);
          }
          return localItem.value;
        }
      }

      return defaultValue;
    } catch (error) {
      console.error('获取缓存失败:', error);
      return defaultValue;
    }
  }

  /**
   * 删除缓存
   * @param {string} key 缓存键
   * @param {Object} options 选项
   * @param {string} options.namespace 命名空间
   */
  delete(key, options = {}) {
    const { namespace = 'default' } = options;
    const fullKey = this.getFullKey(key, namespace);

    try {
      // 删除内存缓存
      if (this.enableMemoryCache) {
        this.memoryCache.delete(fullKey);
      }

      // 删除本地存储缓存
      if (this.enableLocalStorage) {
        localStorage.removeItem(fullKey);
      }

      return true;
    } catch (error) {
      console.error('删除缓存失败:', error);
      return false;
    }
  }

  /**
   * 清空缓存
   * @param {Object} options 选项
   * @param {string} options.namespace 命名空间，不指定则清空所有
   */
  clear(options = {}) {
    const { namespace } = options;

    try {
      if (namespace) {
        // 清空指定命名空间的缓存
        this.clearNamespace(namespace);
      } else {
        // 清空所有缓存
        if (this.enableMemoryCache) {
          this.memoryCache.clear();
        }
        if (this.enableLocalStorage) {
          this.clearAllLocalStorage();
        }
      }

      return true;
    } catch (error) {
      console.error('清空缓存失败:', error);
      return false;
    }
  }

  /**
   * 检查缓存是否存在
   * @param {string} key 缓存键
   * @param {Object} options 选项
   * @param {string} options.namespace 命名空间
   */
  has(key, options = {}) {
    const { namespace = 'default' } = options;
    const fullKey = this.getFullKey(key, namespace);

    try {
      // 检查内存缓存
      if (this.enableMemoryCache) {
        const memoryItem = this.memoryCache.get(fullKey);
        if (memoryItem && !this.isExpired(memoryItem)) {
          return true;
        }
      }

      // 检查本地存储缓存
      if (this.enableLocalStorage) {
        const localItem = this.getLocalStorageCache(fullKey);
        if (localItem && !this.isExpired(localItem)) {
          return true;
        }
      }

      return false;
    } catch (error) {
      console.error('检查缓存失败:', error);
      return false;
    }
  }

  /**
   * 获取缓存统计信息
   */
  getStats() {
    const stats = {
      memoryCache: {
        size: this.memoryCache.size,
        maxSize: this.maxMemorySize
      },
      localStorage: {
        enabled: this.enableLocalStorage,
        size: 0,
        maxSize: this.maxLocalStorageSize
      },
      totalItems: 0,
      expiredItems: 0
    };

    try {
      if (this.enableLocalStorage) {
        stats.localStorage.size = this.getLocalStorageSize();
        stats.totalItems = this.countLocalStorageItems();
        stats.expiredItems = this.countExpiredItems();
      }
    } catch (error) {
      console.error('获取缓存统计失败:', error);
    }

    return stats;
  }

  // 私有方法

  setMemoryCache(key, item) {
    // 检查容量限制
    if (this.memoryCache.size >= this.maxMemorySize) {
      this.evictOldestMemoryItem();
    }
    this.memoryCache.set(key, item);
  }

  getMemoryCache(key) {
    return this.memoryCache.get(key);
  }

  setLocalStorageCache(key, item) {
    try {
      const serialized = JSON.stringify(item);
      
      // 检查存储大小限制
      if (this.getLocalStorageSize() + serialized.length > this.maxLocalStorageSize) {
        this.evictOldestLocalStorageItem();
      }
      
      localStorage.setItem(key, serialized);
    } catch (error) {
      console.error('设置本地存储缓存失败:', error);
      // 如果存储失败，尝试清理一些空间
      this.cleanupLocalStorage();
    }
  }

  getLocalStorageCache(key) {
    try {
      const item = localStorage.getItem(key);
      return item ? JSON.parse(item) : null;
    } catch (error) {
      console.error('获取本地存储缓存失败:', error);
      return null;
    }
  }

  isExpired(item) {
    return Date.now() > item.expiresAt;
  }

  getFullKey(key, namespace) {
    return `${namespace}:${key}`;
  }

  evictOldestMemoryItem() {
    let oldestKey = null;
    let oldestTime = Date.now();

    for (const [key, item] of this.memoryCache.entries()) {
      if (item.timestamp < oldestTime) {
        oldestTime = item.timestamp;
        oldestKey = key;
      }
    }

    if (oldestKey) {
      this.memoryCache.delete(oldestKey);
    }
  }

  evictOldestLocalStorageItem() {
    let oldestKey = null;
    let oldestTime = Date.now();

    for (let i = 0; i < localStorage.length; i++) {
      const key = localStorage.key(i);
      if (key && key.includes(':')) {
        try {
          const item = JSON.parse(localStorage.getItem(key));
          if (item && item.timestamp < oldestTime) {
            oldestTime = item.timestamp;
            oldestKey = key;
          }
        } catch (error) {
          // 忽略解析错误的项目
        }
      }
    }

    if (oldestKey) {
      localStorage.removeItem(oldestKey);
    }
  }

  clearNamespace(namespace) {
    const prefix = `${namespace}:`;

    // 清空内存缓存中的命名空间
    if (this.enableMemoryCache) {
      for (const key of this.memoryCache.keys()) {
        if (key.startsWith(prefix)) {
          this.memoryCache.delete(key);
        }
      }
    }

    // 清空本地存储中的命名空间
    if (this.enableLocalStorage) {
      for (let i = localStorage.length - 1; i >= 0; i--) {
        const key = localStorage.key(i);
        if (key && key.startsWith(prefix)) {
          localStorage.removeItem(key);
        }
      }
    }
  }

  clearAllLocalStorage() {
    try {
      localStorage.clear();
    } catch (error) {
      console.error('清空本地存储失败:', error);
    }
  }

  getLocalStorageSize() {
    let size = 0;
    try {
      for (let i = 0; i < localStorage.length; i++) {
        const key = localStorage.key(i);
        if (key) {
          size += key.length + localStorage.getItem(key).length;
        }
      }
    } catch (error) {
      console.error('计算本地存储大小失败:', error);
    }
    return size;
  }

  countLocalStorageItems() {
    let count = 0;
    try {
      for (let i = 0; i < localStorage.length; i++) {
        const key = localStorage.key(i);
        if (key && key.includes(':')) {
          count++;
        }
      }
    } catch (error) {
      console.error('计算本地存储项目数失败:', error);
    }
    return count;
  }

  countExpiredItems() {
    let count = 0;
    try {
      for (let i = 0; i < localStorage.length; i++) {
        const key = localStorage.key(i);
        if (key && key.includes(':')) {
          const item = this.getLocalStorageCache(key);
          if (item && this.isExpired(item)) {
            count++;
          }
        }
      }
    } catch (error) {
      console.error('计算过期项目数失败:', error);
    }
    return count;
  }

  cleanupLocalStorage() {
    try {
      // 删除过期的项目
      for (let i = localStorage.length - 1; i >= 0; i--) {
        const key = localStorage.key(i);
        if (key && key.includes(':')) {
          const item = this.getLocalStorageCache(key);
          if (item && this.isExpired(item)) {
            localStorage.removeItem(key);
          }
        }
      }
    } catch (error) {
      console.error('清理本地存储失败:', error);
    }
  }

  startCleanupInterval() {
    // 每5分钟清理一次过期缓存
    setInterval(() => {
      this.cleanupExpiredItems();
    }, 5 * 60 * 1000);
  }

  cleanupExpiredItems() {
    // 清理内存缓存中的过期项目
    if (this.enableMemoryCache) {
      for (const [key, item] of this.memoryCache.entries()) {
        if (this.isExpired(item)) {
          this.memoryCache.delete(key);
        }
      }
    }

    // 清理本地存储中的过期项目
    if (this.enableLocalStorage) {
      this.cleanupLocalStorage();
    }
  }
}

// 创建默认缓存实例
const defaultCache = new CacheManager();

// 导出默认实例和类
export default defaultCache;
export { CacheManager };

// 便捷方法
export const cache = {
  set: (key, value, options) => defaultCache.set(key, value, options),
  get: (key, options) => defaultCache.get(key, options),
  delete: (key, options) => defaultCache.delete(key, options),
  clear: (options) => defaultCache.clear(options),
  has: (key, options) => defaultCache.has(key, options),
  getStats: () => defaultCache.getStats()
};

/**
 * 缓存装饰器
 * @param {string} key - 缓存键
 * @param {number} ttl - 过期时间（毫秒）
 * @returns {function} - 装饰后的函数
 */
export function withCache(key, ttl = 5 * 60 * 1000) {
  return function(target, propertyName, descriptor) {
    const method = descriptor.value;
    
    descriptor.value = async function(...args) {
      // 生成缓存键
      const cacheKey = `${key}_${JSON.stringify(args)}`;
      
      // 尝试从缓存获取
      const cached = cache.get(cacheKey);
      if (cached) {
        return cached;
      }
      
      // 执行原方法
      const result = await method.apply(this, args);
      
      // 缓存结果
      cache.set(cacheKey, result, ttl);
      
      return result;
    };
    
    return descriptor;
  };
}

/**
 * 生成缓存键
 * @param {string} prefix - 前缀
 * @param {object} params - 参数对象
 * @returns {string} - 缓存键
 */
export function generateCacheKey(prefix, params = {}) {
  const sortedParams = Object.keys(params)
    .sort()
    .map(key => `${key}:${params[key]}`)
    .join('_');
  
  return `${prefix}_${sortedParams}`;
}

/**
 * 会议列表缓存
 */
export const meetingCache = {
  // 缓存会议列表
  setMeetings(query, data) {
    const key = generateCacheKey('meetings', query);
    cache.set(key, data, 2 * 60 * 1000); // 2分钟过期
  },

  // 获取会议列表
  getMeetings(query) {
    const key = generateCacheKey('meetings', query);
    return cache.get(key);
  },

  // 清除会议相关缓存
  clearMeetings() {
    // 这里可以实现更精确的清除逻辑
    cache.clear();
  }
};

/**
 * 用户信息缓存
 */
export const userCache = {
  // 缓存用户信息
  setUserInfo(username, data) {
    const key = `user_${username}`;
    cache.set(key, data, 10 * 60 * 1000); // 10分钟过期
  },

  // 获取用户信息
  getUserInfo(username) {
    const key = `user_${username}`;
    return cache.get(key);
  },

  // 清除用户缓存
  clearUserInfo(username) {
    const key = `user_${username}`;
    cache.delete(key);
  }
}; 