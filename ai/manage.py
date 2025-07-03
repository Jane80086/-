import os
import sys

def start_all():
    os.system("../start_all.bat")

def stop_all():
    os.system("../stop_all.bat")

def test_all():
    os.system("../test_all.bat")

def push_all():
    os.system("../push_all.bat")

def sync_db_schema():
    os.system("../sync_db_schema.bat")

def setup_all():
    os.system("../setup_all.bat")

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("用法: python manage.py [setup|start|stop|test|push|syncdb]")
    else:
        cmd = sys.argv[1]
        if cmd == "setup":
            setup_all()
        elif cmd == "start":
            start_all()
        elif cmd == "stop":
            stop_all()
        elif cmd == "test":
            test_all()
        elif cmd == "push":
            push_all()
        elif cmd == "syncdb":
            sync_db_schema()
        else:
            print("未知命令") 