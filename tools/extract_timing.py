import json, re
from pathlib import Path
src = Path('src/utils/storage.ts').read_text()
def val(raw):
    raw=raw.strip()
    if raw.startswith('"'): return json.loads(raw)
    if raw.startswith("'"): return raw[1:-1].replace("\\'", "'").replace('\\"','"').replace('\\n','\n')
    return raw
def field(block,name):
    m=re.search(rf'\b{name}:\s*((?:"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\'))',block)
    return val(m.group(1)) if m else ''
def num(block,name):
    m=re.search(rf'\b{name}:\s*([0-9.]+)',block)
    return float(m.group(1)) if m else 0.0
def words_from(raw):
    out=[]
    pat=re.compile(r'\{\s*id:\s*([^,]+),\s*text:\s*((?:"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\')),\s*start:\s*([0-9.]+),\s*end:\s*([0-9.]+)(?:,\s*translation:\s*((?:"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\')))?\s*\}')
    for m in pat.finditer(raw): out.append({'id':val(m.group(1)),'text':val(m.group(2)),'start':float(m.group(3)),'end':float(m.group(4)),'translation':val(m.group(5)) if m.group(5) else ''})
    return out
lesson_re=re.compile(r"^  \{\n    id:\s*('(?:\\.|[^'\\])*'|\"(?:\\.|[^\"\\])*\"),(.*?)(?=^  \},\n  \{|^\];)",re.S|re.M)
lessons=[]
for lm in lesson_re.finditer(src):
    block=lm.group(2); sentences=[]
    sentence_re=re.compile(r'^\s*text:\s*((?:"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\')),(.*?^\s{8}\]\s*,?\s*\n\s{6}\})',re.S|re.M)
    for sm in sentence_re.finditer(block):
        sb=sm.group(2)
        wm=re.search(r'words:\s*\[(.*?)^\s{8}\]',sb,re.S|re.M)
        sentences.append({'text':val(sm.group(1)),'arabic':field(sb,'arabic'),'start':num(sb,'start'),'end':num(sb,'end'),'words':words_from(wm.group(1) if wm else '')})
    lessons.append({'id':val(lm.group(1)),'title':field(block,'title'),'arabicTitle':field(block,'arabicTitle'),'sentences':sentences})
Path('android/app/src/main/assets').mkdir(parents=True,exist_ok=True)
Path('android/app/src/main/assets/lessons.json').write_text(json.dumps(lessons,ensure_ascii=False,indent=2))
print(json.dumps({'lessons':len(lessons),'sentences':sum(len(x['sentences']) for x in lessons),'words':sum(len(s['words']) for x in lessons for s in x['sentences'])},ensure_ascii=False))
