# Python虚拟环境

这个目录用于存放Python虚拟环境。

## 创建虚拟环境

```bash
# 使用venv创建虚拟环境
python -m venv .venv

# 激活虚拟环境 (Windows)
.venv\Scripts\activate

# 激活虚拟环境 (Linux/Mac)
source .venv/bin/activate
```

## 安装依赖

```bash
# 安装项目依赖
pip install -r requirements.txt

# 或者使用pyproject.toml
pip install -e .
```

## 在IDEA中使用虚拟环境

1. 打开IDEA设置 (`Ctrl + Alt + S`)
2. 导航到 `Project: course_manager` → `Python Interpreter`
3. 点击齿轮图标 → `Add`
4. 选择 `Existing Environment`
 