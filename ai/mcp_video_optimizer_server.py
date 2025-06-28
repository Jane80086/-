"""
MCP Video Optimizer Server
为课程管理系统提供视频优化和处理服务
"""

from typing import Optional, List, Dict, Any
from mcp.server.fastmcp import FastMCP
from pydantic import BaseModel, Field
import requests
import json
from datetime import datetime
import os

# 创建MCP服务器
mcp = FastMCP("Video Optimizer Server", dependencies=["requests"])

# API配置
API_BASE_URL = "http://localhost:8080/api"
MINIO_BASE_URL = "http://localhost:9000"

class VideoOptimizationRequest(BaseModel):
    """视频优化请求"""
    video_url: str = Field(..., description="视频URL")
    target_format: str = Field(..., description="目标格式")
    quality: str = Field(..., description="质量等级")
    resolution: Optional[str] = Field(None, description="目标分辨率")
    bitrate: Optional[int] = Field(None, description="目标比特率")

class VideoOptimizationResult(BaseModel):
    """视频优化结果"""
    original_url: str = Field(..., description="原始视频URL")
    optimized_url: str = Field(..., description="优化后视频URL")
    original_size: int = Field(..., description="原始文件大小（字节）")
    optimized_size: int = Field(..., description="优化后文件大小（字节）")
    compression_ratio: float = Field(..., description="压缩比例")
    processing_time: float = Field(..., description="处理时间（秒）")
    status: str = Field(..., description="处理状态")

class VideoInfo(BaseModel):
    """视频信息"""
    url: str = Field(..., description="视频URL")
    filename: str = Field(..., description="文件名")
    format: str = Field(..., description="格式")
    duration: float = Field(..., description="时长（秒）")
    resolution: str = Field(..., description="分辨率")
    bitrate: int = Field(..., description="比特率")
    size: int = Field(..., description="文件大小（字节）")
    fps: float = Field(..., description="帧率")

@mcp.tool()
def optimize_video(
    video_url: str,
    target_format: str = "mp4",
    quality: str = "medium",
    resolution: Optional[str] = None,
    bitrate: Optional[int] = None
) -> VideoOptimizationResult:
    """
    优化视频文件
    
    Args:
        video_url: 视频URL
        target_format: 目标格式（mp4, webm, avi等）
        quality: 质量等级（low, medium, high）
        resolution: 目标分辨率（如1920x1080）
        bitrate: 目标比特率（kbps）
    
    Returns:
        视频优化结果
    """
    try:
        request_data = {
            "video_url": video_url,
            "target_format": target_format,
            "quality": quality
        }
        
        if resolution:
            request_data["resolution"] = resolution
        if bitrate:
            request_data["bitrate"] = bitrate
        
        response = requests.post(
            f"{API_BASE_URL}/video/optimize",
            json=request_data
        )
        response.raise_for_status()
        
        data = response.json()
        
        return VideoOptimizationResult(
            original_url=data.get("original_url", video_url),
            optimized_url=data.get("optimized_url", ""),
            original_size=data.get("original_size", 0),
            optimized_size=data.get("optimized_size", 0),
            compression_ratio=data.get("compression_ratio", 0.0),
            processing_time=data.get("processing_time", 0.0),
            status=data.get("status", "completed")
        )
        
    except Exception as e:
        print(f"优化视频时出错: {e}")
        return VideoOptimizationResult(
            original_url=video_url,
            optimized_url="",
            original_size=0,
            optimized_size=0,
            compression_ratio=0.0,
            processing_time=0.0,
            status="failed"
        )

@mcp.tool()
def get_video_info(video_url: str) -> Optional[VideoInfo]:
    """
    获取视频信息
    
    Args:
        video_url: 视频URL
    
    Returns:
        视频信息
    """
    try:
        params = {"url": video_url}
        
        response = requests.get(f"{API_BASE_URL}/video/info", params=params)
        response.raise_for_status()
        
        data = response.json()
        
        return VideoInfo(
            url=data.get("url", video_url),
            filename=data.get("filename", ""),
            format=data.get("format", ""),
            duration=data.get("duration", 0.0),
            resolution=data.get("resolution", ""),
            bitrate=data.get("bitrate", 0),
            size=data.get("size", 0),
            fps=data.get("fps", 0.0)
        )
        
    except Exception as e:
        print(f"获取视频信息时出错: {e}")
        return None

@mcp.tool()
def compress_video(
    video_url: str,
    target_size_mb: int,
    quality: str = "medium"
) -> VideoOptimizationResult:
    """
    压缩视频到指定大小
    
    Args:
        video_url: 视频URL
        target_size_mb: 目标大小（MB）
        quality: 质量等级
    
    Returns:
        压缩结果
    """
    try:
        request_data = {
            "video_url": video_url,
            "target_size_mb": target_size_mb,
            "quality": quality
        }
        
        response = requests.post(
            f"{API_BASE_URL}/video/compress",
            json=request_data
        )
        response.raise_for_status()
        
        data = response.json()
        
        return VideoOptimizationResult(
            original_url=data.get("original_url", video_url),
            optimized_url=data.get("optimized_url", ""),
            original_size=data.get("original_size", 0),
            optimized_size=data.get("optimized_size", 0),
            compression_ratio=data.get("compression_ratio", 0.0),
            processing_time=data.get("processing_time", 0.0),
            status=data.get("status", "completed")
        )
        
    except Exception as e:
        print(f"压缩视频时出错: {e}")
        return VideoOptimizationResult(
            original_url=video_url,
            optimized_url="",
            original_size=0,
            optimized_size=0,
            compression_ratio=0.0,
            processing_time=0.0,
            status="failed"
        )

@mcp.tool()
def convert_video_format(
    video_url: str,
    target_format: str,
    quality: str = "medium"
) -> VideoOptimizationResult:
    """
    转换视频格式
    
    Args:
        video_url: 视频URL
        target_format: 目标格式
        quality: 质量等级
    
    Returns:
        转换结果
    """
    try:
        request_data = {
            "video_url": video_url,
            "target_format": target_format,
            "quality": quality
        }
        
        response = requests.post(
            f"{API_BASE_URL}/video/convert",
            json=request_data
        )
        response.raise_for_status()
        
        data = response.json()
        
        return VideoOptimizationResult(
            original_url=data.get("original_url", video_url),
            optimized_url=data.get("optimized_url", ""),
            original_size=data.get("original_size", 0),
            optimized_size=data.get("optimized_size", 0),
            compression_ratio=data.get("compression_ratio", 0.0),
            processing_time=data.get("processing_time", 0.0),
            status=data.get("status", "completed")
        )
        
    except Exception as e:
        print(f"转换视频格式时出错: {e}")
        return VideoOptimizationResult(
            original_url=video_url,
            optimized_url="",
            original_size=0,
            optimized_size=0,
            compression_ratio=0.0,
            processing_time=0.0,
            status="failed"
        )

@mcp.tool()
def extract_video_thumbnail(
    video_url: str,
    time_position: float = 10.0,
    output_format: str = "jpg"
) -> Dict[str, Any]:
    """
    提取视频缩略图
    
    Args:
        video_url: 视频URL
        time_position: 提取时间点（秒）
        output_format: 输出格式（jpg, png）
    
    Returns:
        缩略图信息
    """
    try:
        request_data = {
            "video_url": video_url,
            "time_position": time_position,
            "output_format": output_format
        }
        
        response = requests.post(
            f"{API_BASE_URL}/video/thumbnail",
            json=request_data
        )
        response.raise_for_status()
        
        return response.json()
        
    except Exception as e:
        print(f"提取视频缩略图时出错: {e}")
        return {"thumbnail_url": "", "status": "failed"}

@mcp.tool()
def get_optimization_status(task_id: str) -> Dict[str, Any]:
    """
    获取视频优化任务状态
    
    Args:
        task_id: 任务ID
    
    Returns:
        任务状态信息
    """
    try:
        response = requests.get(f"{API_BASE_URL}/video/status/{task_id}")
        response.raise_for_status()
        
        return response.json()
        
    except Exception as e:
        print(f"获取优化状态时出错: {e}")
        return {"status": "unknown", "progress": 0}

@mcp.tool()
def get_supported_formats() -> List[str]:
    """
    获取支持的视频格式列表
    
    Returns:
        支持的格式列表
    """
    try:
        response = requests.get(f"{API_BASE_URL}/video/formats")
        response.raise_for_status()
        
        return response.json()
        
    except Exception as e:
        print(f"获取支持格式时出错: {e}")
        return ["mp4", "avi", "mov", "wmv", "flv", "webm"]

@mcp.tool()
def batch_optimize_videos(
    video_urls: List[str],
    target_format: str = "mp4",
    quality: str = "medium"
) -> List[VideoOptimizationResult]:
    """
    批量优化视频
    
    Args:
        video_urls: 视频URL列表
        target_format: 目标格式
        quality: 质量等级
    
    Returns:
        批量优化结果
    """
    try:
        request_data = {
            "video_urls": video_urls,
            "target_format": target_format,
            "quality": quality
        }
        
        response = requests.post(
            f"{API_BASE_URL}/video/batch-optimize",
            json=request_data
        )
        response.raise_for_status()
        
        data = response.json()
        results = []
        
        for item in data:
            result = VideoOptimizationResult(
                original_url=item.get("original_url", ""),
                optimized_url=item.get("optimized_url", ""),
                original_size=item.get("original_size", 0),
                optimized_size=item.get("optimized_size", 0),
                compression_ratio=item.get("compression_ratio", 0.0),
                processing_time=item.get("processing_time", 0.0),
                status=item.get("status", "failed")
            )
            results.append(result)
        
        return results
        
    except Exception as e:
        print(f"批量优化视频时出错: {e}")
        return []

if __name__ == "__main__":
    mcp.run() 