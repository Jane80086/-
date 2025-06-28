/**
 * 增强的数据验证工具类
 * 提供全面的数据验证功能，包括类型检查、格式验证、业务规则验证等
 */

// 正则表达式模式
const PATTERNS = {
  EMAIL: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
  PHONE: /^1[3-9]\d{9}$/,
  USERNAME: /^[a-zA-Z0-9_]{3,20}$/,
  PASSWORD: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{8,20}$/,
  URL: /^https?:\/\/.+/,
  DATE: /^\d{4}-\d{2}-\d{2}$/,
  DATETIME: /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}$/,
  CHINESE: /^[\u4e00-\u9fa5]+$/,
  ALPHANUMERIC: /^[a-zA-Z0-9]+$/,
  MEETING_NAME: /^[\u4e00-\u9fa5a-zA-Z0-9\s\-_()（）]{2,100}$/
};

// 验证规则配置
const VALIDATION_RULES = {
  MEETING: {
    name: {
      required: true,
      minLength: 2,
      maxLength: 100,
      pattern: PATTERNS.MEETING_NAME
    },
    content: {
      required: true,
      minLength: 10,
      maxLength: 2000
    },
    startTime: {
      required: true,
      type: 'datetime',
      future: true
    },
    endTime: {
      required: true,
      type: 'datetime',
      future: true
    }
  },
  USER: {
    username: {
      required: true,
      pattern: PATTERNS.USERNAME
    },
    password: {
      required: true,
      pattern: PATTERNS.PASSWORD
    },
    realName: {
      required: true,
      maxLength: 50
    },
    email: {
      required: false,
      pattern: PATTERNS.EMAIL
    },
    phone: {
      required: false,
      pattern: PATTERNS.PHONE
    }
  }
};

class Validator {
  constructor() {
    this.errors = [];
    this.customRules = new Map();
  }

  /**
   * 验证单个字段
   * @param {any} value 要验证的值
   * @param {Object} rules 验证规则
   * @param {string} fieldName 字段名称
   * @returns {boolean} 验证是否通过
   */
  validateField(value, rules, fieldName = '') {
    const fieldErrors = [];

    // 必填验证
    if (rules.required && this.isEmpty(value)) {
      fieldErrors.push(`${fieldName || '字段'}不能为空`);
    }

    // 如果值为空且不是必填，跳过其他验证
    if (this.isEmpty(value) && !rules.required) {
      return true;
    }

    // 类型验证
    if (rules.type && !this.validateType(value, rules.type)) {
      fieldErrors.push(`${fieldName || '字段'}类型不正确`);
    }

    // 长度验证
    if (rules.minLength && this.getStringLength(value) < rules.minLength) {
      fieldErrors.push(`${fieldName || '字段'}长度不能少于${rules.minLength}个字符`);
    }

    if (rules.maxLength && this.getStringLength(value) > rules.maxLength) {
      fieldErrors.push(`${fieldName || '字段'}长度不能超过${rules.maxLength}个字符`);
    }

    // 数值范围验证
    if (rules.min !== undefined && this.toNumber(value) < rules.min) {
      fieldErrors.push(`${fieldName || '字段'}不能小于${rules.min}`);
    }

    if (rules.max !== undefined && this.toNumber(value) > rules.max) {
      fieldErrors.push(`${fieldName || '字段'}不能大于${rules.max}`);
    }

    // 模式验证
    if (rules.pattern && !rules.pattern.test(String(value))) {
      fieldErrors.push(`${fieldName || '字段'}格式不正确`);
    }

    // 自定义验证
    if (rules.validator && typeof rules.validator === 'function') {
      try {
        const result = rules.validator(value);
        if (result !== true) {
          fieldErrors.push(result || `${fieldName || '字段'}验证失败`);
        }
      } catch (error) {
        fieldErrors.push(`${fieldName || '字段'}验证出错: ${error.message}`);
      }
    }

    // 日期时间验证
    if (rules.future && !this.isFutureDateTime(value)) {
      fieldErrors.push(`${fieldName || '字段'}必须是未来时间`);
    }

    if (rules.past && !this.isPastDateTime(value)) {
      fieldErrors.push(`${fieldName || '字段'}必须是过去时间`);
    }

    // 枚举验证
    if (rules.enum && !rules.enum.includes(value)) {
      fieldErrors.push(`${fieldName || '字段'}必须是以下值之一: ${rules.enum.join(', ')}`);
    }

    // 添加错误到全局错误列表
    if (fieldErrors.length > 0) {
      this.errors.push({
        field: fieldName,
        errors: fieldErrors
      });
      return false;
    }

    return true;
  }

  /**
   * 验证对象
   * @param {Object} data 要验证的数据对象
   * @param {Object} schema 验证模式
   * @returns {boolean} 验证是否通过
   */
  validateObject(data, schema) {
    this.errors = [];
    let isValid = true;

    for (const [fieldName, rules] of Object.entries(schema)) {
      const value = data[fieldName];
      if (!this.validateField(value, rules, fieldName)) {
        isValid = false;
      }
    }

    return isValid;
  }

  /**
   * 验证数组
   * @param {Array} array 要验证的数组
   * @param {Object} itemSchema 数组项的验证模式
   * @param {string} arrayName 数组名称
   * @returns {boolean} 验证是否通过
   */
  validateArray(array, itemSchema, arrayName = '数组') {
    this.errors = [];
    let isValid = true;

    if (!Array.isArray(array)) {
      this.errors.push({
        field: arrayName,
        errors: [`${arrayName}必须是数组类型`]
      });
      return false;
    }

    array.forEach((item, index) => {
      if (!this.validateObject(item, itemSchema)) {
        this.errors.push({
          field: `${arrayName}[${index}]`,
          errors: this.errors
        });
        isValid = false;
      }
    });

    return isValid;
  }

  /**
   * 获取验证错误
   * @returns {Array} 错误列表
   */
  getErrors() {
    return this.errors;
  }

  /**
   * 获取格式化的错误信息
   * @returns {string} 格式化的错误信息
   */
  getErrorMessage() {
    return this.errors
      .map(error => `${error.field}: ${error.errors.join(', ')}`)
      .join('; ');
  }

  /**
   * 清除错误
   */
  clearErrors() {
    this.errors = [];
  }

  /**
   * 添加自定义验证规则
   * @param {string} ruleName 规则名称
   * @param {Function} validator 验证函数
   */
  addCustomRule(ruleName, validator) {
    this.customRules.set(ruleName, validator);
  }

  // 辅助方法

  isEmpty(value) {
    if (value === null || value === undefined) {
      return true;
    }
    if (typeof value === 'string') {
      return value.trim() === '';
    }
    if (Array.isArray(value)) {
      return value.length === 0;
    }
    if (typeof value === 'object') {
      return Object.keys(value).length === 0;
    }
    return false;
  }

  validateType(value, type) {
    switch (type) {
      case 'string':
        return typeof value === 'string';
      case 'number':
        return typeof value === 'number' && !isNaN(value);
      case 'boolean':
        return typeof value === 'boolean';
      case 'array':
        return Array.isArray(value);
      case 'object':
        return typeof value === 'object' && value !== null && !Array.isArray(value);
      case 'email':
        return PATTERNS.EMAIL.test(String(value));
      case 'phone':
        return PATTERNS.PHONE.test(String(value));
      case 'url':
        return PATTERNS.URL.test(String(value));
      case 'date':
        return PATTERNS.DATE.test(String(value)) && !isNaN(Date.parse(value));
      case 'datetime':
        return PATTERNS.DATETIME.test(String(value)) && !isNaN(Date.parse(value));
      default:
        return true;
    }
  }

  getStringLength(value) {
    if (typeof value === 'string') {
      return value.length;
    }
    if (typeof value === 'number') {
      return String(value).length;
    }
    return 0;
  }

  toNumber(value) {
    const num = Number(value);
    return isNaN(num) ? 0 : num;
  }

  isFutureDateTime(value) {
    const date = new Date(value);
    return !isNaN(date.getTime()) && date > new Date();
  }

  isPastDateTime(value) {
    const date = new Date(value);
    return !isNaN(date.getTime()) && date < new Date();
  }
}

// 创建默认验证器实例
const validator = new Validator();

// 预定义的验证函数

/**
 * 验证会议数据
 * @param {Object} meeting 会议数据
 * @returns {Object} 验证结果
 */
export const validateMeeting = (meeting) => {
  const result = {
    isValid: false,
    errors: []
  };

  if (!meeting) {
    result.errors.push('会议数据不能为空');
    return result;
  }

  result.isValid = validator.validateObject(meeting, VALIDATION_RULES.MEETING);
  result.errors = validator.getErrors();

  // 额外的业务逻辑验证
  if (meeting.startTime && meeting.endTime) {
    const startTime = new Date(meeting.startTime);
    const endTime = new Date(meeting.endTime);
    
    if (startTime >= endTime) {
      result.isValid = false;
      result.errors.push({
        field: 'endTime',
        errors: ['结束时间必须晚于开始时间']
      });
    }

    const duration = endTime.getTime() - startTime.getTime();
    const minDuration = 15 * 60 * 1000; // 15分钟
    const maxDuration = 24 * 60 * 60 * 1000; // 24小时

    if (duration < minDuration) {
      result.isValid = false;
      result.errors.push({
        field: 'duration',
        errors: ['会议时长不能少于15分钟']
      });
    }

    if (duration > maxDuration) {
      result.isValid = false;
      result.errors.push({
        field: 'duration',
        errors: ['会议时长不能超过24小时']
      });
    }
  }

  return result;
};

/**
 * 验证用户数据
 * @param {Object} user 用户数据
 * @returns {Object} 验证结果
 */
export const validateUser = (user) => {
  const result = {
    isValid: false,
    errors: []
  };

  if (!user) {
    result.errors.push('用户数据不能为空');
    return result;
  }

  result.isValid = validator.validateObject(user, VALIDATION_RULES.USER);
  result.errors = validator.getErrors();

  return result;
};

/**
 * 验证查询参数
 * @param {Object} query 查询参数
 * @returns {Object} 验证结果
 */
export const validateQuery = (query) => {
  const result = {
    isValid: false,
    errors: []
  };

  if (!query) {
    result.errors.push('查询参数不能为空');
    return result;
  }

  const querySchema = {
    page: {
      type: 'number',
      min: 1,
      max: 1000
    },
    size: {
      type: 'number',
      min: 1,
      max: 100
    },
    meetingName: {
      required: false,
      maxLength: 100
    },
    creator: {
      required: false,
      maxLength: 100
    }
  };

  result.isValid = validator.validateObject(query, querySchema);
  result.errors = validator.getErrors();

  return result;
};

/**
 * 验证审核请求
 * @param {Object} reviewRequest 审核请求
 * @returns {Object} 验证结果
 */
export const validateReviewRequest = (reviewRequest) => {
  const result = {
    isValid: false,
    errors: []
  };

  if (!reviewRequest) {
    result.errors.push('审核请求不能为空');
    return result;
  }

  const reviewSchema = {
    meetingId: {
      required: true,
      type: 'number',
      min: 1
    },
    status: {
      required: true,
      enum: [1, 2] // 1: 通过, 2: 拒绝
    },
    reviewComment: {
      required: false,
      maxLength: 500
    }
  };

  result.isValid = validator.validateObject(reviewRequest, reviewSchema);
  result.errors = validator.getErrors();

  return result;
};

// 便捷验证函数

/**
 * 验证邮箱格式
 * @param {string} email 邮箱地址
 * @returns {boolean} 是否有效
 */
export const isValidEmail = (email) => {
  return PATTERNS.EMAIL.test(email);
};

/**
 * 验证手机号格式
 * @param {string} phone 手机号
 * @returns {boolean} 是否有效
 */
export const isValidPhone = (phone) => {
  return PATTERNS.PHONE.test(phone);
};

/**
 * 验证用户名格式
 * @param {string} username 用户名
 * @returns {boolean} 是否有效
 */
export const isValidUsername = (username) => {
  return PATTERNS.USERNAME.test(username);
};

/**
 * 验证密码强度
 * @param {string} password 密码
 * @returns {boolean} 是否有效
 */
export const isValidPassword = (password) => {
  return PATTERNS.PASSWORD.test(password);
};

/**
 * 验证URL格式
 * @param {string} url URL地址
 * @returns {boolean} 是否有效
 */
export const isValidUrl = (url) => {
  return PATTERNS.URL.test(url);
};

/**
 * 验证日期格式
 * @param {string} date 日期字符串
 * @returns {boolean} 是否有效
 */
export const isValidDate = (date) => {
  return PATTERNS.DATE.test(date) && !isNaN(Date.parse(date));
};

/**
 * 验证日期时间格式
 * @param {string} datetime 日期时间字符串
 * @returns {boolean} 是否有效
 */
export const isValidDateTime = (datetime) => {
  return PATTERNS.DATETIME.test(datetime) && !isNaN(Date.parse(datetime));
};

// 导出
export default validator;
export { Validator, PATTERNS, VALIDATION_RULES }; 