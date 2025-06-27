#!/usr/bin/env python3
"""
启动MCP课程优化服务器
"""

import subprocess
import sys
import time
import requests
from pathlib import Path

def check_dependencies():
    """检查依赖是否安装"""
    try:
        import mcp
        import requests
        print("✅ 依赖检查通过")
        return True
    except ImportError as e:
        print(f"❌ 缺少依赖: {e}")
        print("请运行: pip install mcp requests")
        return False

def start_mcp_server():
    """启动MCP服务器"""
    print("🚀 启动MCP课程优化服务器...")
    
    try:
        # 启动MCP服务器
        process = subprocess.Popen([
            sys.executable, "mcp_course_optimizer.py"
        ], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        
        # 等待服务器启动
        time.sleep(3)
        
        # 检查服务器是否启动成功
        try:
            response = requests.get("http://localhost:6277/health", timeout=5)
            if response.status_code == 200:
                print("✅ MCP服务器启动成功!")
                print("📡 服务器地址: http://localhost:6277")
                print("🔧 可用工具:")
                print("  - analyze_course_heat: 分析课程热度")
                print("  - generate_course_recommendations: 生成课程推荐")
                print("  - update_featured_courses: 更新推荐课程")
                return process
            else:
                print("❌ MCP服务器启动失败")
                return None
        except requests.exceptions.RequestException:
            print("❌ 无法连接到MCP服务器")
            return None
            
    except Exception as e:
        print(f"❌ 启动MCP服务器时出错: {e}")
        return None

def main():
    """主函数"""
    print("=" * 50)
    print("MCP课程优化服务器启动器")
    print("=" * 50)
    
    # 检查依赖
    if not check_dependencies():
        return
    
    # 启动服务器
    process = start_mcp_server()
    
    if process:
        try:
            print("\n按 Ctrl+C 停止服务器...")
            process.wait()
        except KeyboardInterrupt:
            print("\n🛑 正在停止服务器...")
            process.terminate()
            process.wait()
            print("✅ 服务器已停止")
    else:
        print("❌ 服务器启动失败")

if __name__ == "__main__":
    main() 