import request from './index'

class FileService {
    /**
     * 获取单个文件的预签名URL
     * @param {string} objectName - 文件对象名称
     * @returns {Promise<string>} 预签名URL
     */
    async getPresignedUrl(objectName) {
        try {
            const response = await request.get(`/api/file/presigned-url/${encodeURIComponent(objectName)}`);
            if (response.code === 200) {
                return response.data;
            } else {
                throw new Error(response.message || '获取预签名URL失败');
            }
        } catch (error) {
            console.error('获取预签名URL失败:', error);
            throw error;
        }
    }

    /**
     * 批量获取文件的预签名URL
     * @param {string[]} objectNames - 文件对象名称列表
     * @returns {Promise<Object>} 对象名称到预签名URL的映射
     */
    async getBatchPresignedUrls(objectNames) {
        try {
            const response = await request.post('/api/file/presigned-urls', objectNames);
            if (response.code === 200) {
                return response.data;
            } else {
                throw new Error(response.message || '批量获取预签名URL失败');
            }
        } catch (error) {
            console.error('批量获取预签名URL失败:', error);
            throw error;
        }
    }

    /**
     * 检查文件是否存在
     * @param {string} objectName - 文件对象名称
     * @returns {Promise<boolean>} 文件是否存在
     */
    async checkFileExists(objectName) {
        try {
            const response = await request.get(`/api/file/exists/${encodeURIComponent(objectName)}`);
            if (response.code === 200) {
                return response.data;
            } else {
                throw new Error(response.message || '检查文件存在性失败');
            }
        } catch (error) {
            console.error('检查文件存在性失败:', error);
            return false;
        }
    }

    /**
     * 删除文件
     * @param {string} objectName - 文件对象名称
     * @returns {Promise<boolean>} 删除是否成功
     */
    async deleteFile(objectName) {
        try {
            const response = await request.delete(`/api/file/${encodeURIComponent(objectName)}`);
            if (response.code === 200) {
                return response.data;
            } else {
                throw new Error(response.message || '删除文件失败');
            }
        } catch (error) {
            console.error('删除文件失败:', error);
            throw error;
        }
    }

    /**
     * 获取图片的预签名URL并缓存
     * @param {string} objectName - 文件对象名称
     * @returns {Promise<string>} 预签名URL
     */
    async getImageUrl(objectName) {
        if (!objectName) {
            return null;
        }

        // 如果已经是完整的URL，直接返回
        if (objectName.startsWith('http://') || objectName.startsWith('https://')) {
            return objectName;
        }

        // 检查缓存
        const cacheKey = `image_url_${objectName}`;
        const cachedUrl = sessionStorage.getItem(cacheKey);
        if (cachedUrl) {
            return cachedUrl;
        }

        try {
            const presignedUrl = await this.getPresignedUrl(objectName);
            // 缓存1小时（预签名URL通常1小时过期）
            sessionStorage.setItem(cacheKey, presignedUrl);
            // 设置过期时间
            setTimeout(() => {
                sessionStorage.removeItem(cacheKey);
            }, 55 * 60 * 1000); // 55分钟后清除缓存
            
            return presignedUrl;
        } catch (error) {
            console.error('获取图片URL失败:', error);
            return null;
        }
    }

    /**
     * 批量获取图片URL
     * @param {string[]} objectNames - 文件对象名称列表
     * @returns {Promise<Object>} 对象名称到URL的映射
     */
    async getBatchImageUrls(objectNames) {
        if (!objectNames || objectNames.length === 0) {
            return {};
        }

        const result = {};
        const needPresignedUrls = [];

        // 分离需要预签名URL的对象名称
        for (const objectName of objectNames) {
            if (objectName.startsWith('http://') || objectName.startsWith('https://')) {
                result[objectName] = objectName;
            } else {
                needPresignedUrls.push(objectName);
            }
        }

        if (needPresignedUrls.length > 0) {
            try {
                const presignedUrls = await this.getBatchPresignedUrls(needPresignedUrls);
                Object.assign(result, presignedUrls);
            } catch (error) {
                console.error('批量获取图片URL失败:', error);
            }
        }

        return result;
    }

    /**
     * 上传文件
     * @param {File} file - 要上传的文件
     * @param {string} type - 文件类型，默认为'course'
     * @returns {Promise<Object>} 上传结果
     */
    async uploadFile(file, type = 'course') {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('type', type);
        
        return request.post('/api/file/upload', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
    }
}

export const fileAPI = new FileService();
export default fileAPI; 