import os

def is_gbk_encoded(content):
    try:
        content.decode('utf-8')
        return False
    except UnicodeDecodeError:
        try:
            content.decode('gbk')
            return True
        except UnicodeDecodeError:
            return False

def fix_file_encoding(file_path):
    with open(file_path, 'rb') as f:
        content = f.read()
    if is_gbk_encoded(content):
        try:
            text = content.decode('gbk')
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(text)
            print(f'[修复] {file_path}')
        except Exception as e:
            print(f'[跳过] {file_path} ({e})')
    else:
        if b'\xef\xbf\xbd' in content:
            print(f'[警告] {file_path} 可能含有乱码字符""')

def scan_and_fix(root='.'): 
    for dirpath, _, filenames in os.walk(root):
        for name in filenames:
            if name.endswith(('.java', '.xml', '.yml', '.properties')):
                fix_file_encoding(os.path.join(dirpath, name))

if __name__ == '__main__':
    scan_and_fix('.')
    print('全部检测与修复完成！') 