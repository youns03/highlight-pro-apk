import json, re, sys
from pathlib import Path
src = Path('src/utils/storage.ts').read_text()
native = json.loads(Path('android/app/src/main/assets/lessons.json').read_text())
errors=[]
source_ids=re.findall(r"^    id:\s*'([^']+)'",src,re.M)
if source_ids != [x['id'] for x in native]: errors.append(f'lesson IDs differ: source={source_ids} native={[x["id"] for x in native]}')
def quoted_values(name):
    return re.findall(rf'^\s+{name}:\s*("(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\')',src,re.M)
def unquote(v): return json.loads(v) if v.startswith('"') else v[1:-1].replace("\\'", "'")
source_text=[unquote(v) for v in quoted_values('text')]
source_ar=[unquote(v) for v in quoted_values('arabic')]
native_sentences=[s for l in native for s in l['sentences']]
if source_text != [s['text'] for s in native_sentences]: errors.append('French sentence text differs')
if source_ar != [s['arabic'] for s in native_sentences]: errors.append('Arabic sentence text differs')
source_word_ids=re.findall(r"\{ id:\s*'?(w-[^,'\"]+)",src)
native_word_ids=[w['id'] for s in native_sentences for w in s['words']]
if source_word_ids != native_word_ids: errors.append('word IDs differ')
if len(native) != 7 or len(native_sentences) != 17 or len(native_word_ids) != 158: errors.append('unexpected corpus counts')
expected_meta=[('A1','fr_female_celeste',42.0,37.0,True),('A1','fr_male_remy',38.0,25.0,False),('A2','fr_female_celeste',35.0,18.0,False),('A1','fr_male_remy',32.0,10.0,False),('A2','fr_female_celeste',40.0,8.0,False),('B1','fr_male_remy',48.0,54.0,True),('B2','fr_female_celeste',55.0,72.0,False)]
for i,(level,voice,duration,progress,bookmarked) in enumerate(expected_meta):
    x=native[i]
    if (x['level'],x['voiceId'],x['duration'],x['progressPercent'],x['bookmarked']) != (level,voice,duration,progress,bookmarked): errors.append(f'metadata differs at {x["id"]}')
if errors:
    print('\n'.join(errors)); sys.exit(1)
print(f'Native parity OK: {len(native)} lessons, {len(native_sentences)} sentences, {len(native_word_ids)} words; metadata and text match.')
