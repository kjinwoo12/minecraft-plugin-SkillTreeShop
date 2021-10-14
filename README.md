# minecraft-plugin-SkillTreeShop
Spigot Version : 1.16.4

# Documents

## Organized requirements | 요구사항 정의
### 용어 정리
- GUI : 6줄짜리 인벤토리 화면
- SkillTree : 서버에 내부적으로 저장되는 GUI 아이템 배치
- Skill : SkillTree에 저장되는 아이템
### 기능 정리
- SkillTree 생성
  - 오피 권한 필요
  - `/스킬트리 생성 <이름>`을 입력하면 `<이름>` 스킬트리가 서버 내부에 생성
  - 개발자 추가 코멘트
      - 생성 후 자동으로 SkillTree 설정할 수 있음
      - 명령어 입력 후 스킬트리가 이미 있을 때 `<이름>은 이미 생성된 스킬트리입니다` 메시지 출력
- SkillTree 삭제 (개발자 추가)
  - 오피 권한 필요
  - `/스킬트리 삭제 <이름>`을 입력하면 `<이름>`에 해당하는 스킬트리가 서버에서 삭제
  - 명령어 입력 후 스킬트리가 존재하지 않으면 `<이름>은 존재하지 않는 스킬트리입니다.` 메시지 출력
- SkillTree 설정
  - 오피 권한 필요
  - `/스킬트리 설정 <이름>`을 입력하면 `<이름>`에 해당하는 스킬트리가 열림
  - 스킬트리에 아이템을 자유롭게 배치할 수 있음
  - 스킬트리가 꺼지면 `<이름>`에 해당하는 스킬트리를 서버 내부에 저장
- SkillTree 확인
  - `/스킬트리 확인 <이름>`을 입력하면 `<이름>`에 해당하는 스킬트리가 열림
  - 스킬트리의 아이템을 구매/판매 할 수 있음
  - 각 줄의 아이템은 윗 줄의 아이템을 구매해야 구매할 수 있음 
- Skill 구매 설정
  - 오피 권한 필요
  - `/스킬트리 구매가격설정 <이름>`을 입력하면 `<이름>`에 해당하는 스킬트리가 열림
  - 스킬트리에서 아이템을 클릭하면 `아이템 구매가격을 채팅으로 입력해주세요.` 출력 후 GUI가 닫힘
  - 채팅창에 가격을 입력하면 해당 스킬의 아이템 가격이 설정되며 `구매가격 설정이 완료되었습니다.` 메시지 출력
  - 잘못된 값이 입력되면 `가격 설정이 취소되었습니다.` 출력
- Skill 판매 설정
  - `/스킬트리 판매가격설정 <이름>`을 입력하면 `<이름>`에 해당하는 스킬트리가 열림
  - GUI에서 아이템을 클릭하면 `아이템 판매가격을 채팅으로 입력해주세요.` 출력 후 GUI가 닫힘
  - 채팅창에 가격을 입력하면 해당 스킬의 아이템 가격이 설정되며 `판매가격 설정이 완료되었습니다.` 메시지 출력
  - 잘못된 값이 입력되면 `가격 설정이 취소되었습니다.` 출력
- Skill 구매 금지 설정
  - 오피 권한 필요
  - `/스킬트리 구매금지 <이름>`을 입력하면 `<이름>`에 해당하는 스킬트리가 열림
  - 스킬트리의 아이템을 클릭하면 해당 아이템은 구매할 수 없도록 설정
- Skill 구매 허용 설정
  - 오피 권한 필요
  - `/스킬트리 구매허용 <이름>`을 입력하면 `<이름>`에 해당하는 스킬트리가 열림
  - 스킬트리의 아이템을 클릭하면 해당 아이템은 구매할 수 있도록 설정
- Skill 판매 금지 설정
  - `/스킬트리 판매금지 <이름>`을 입력하면 `<이름>`에 해당하는 스킬트리가 열림
  - 스킬트리의 아이템을 클릭하면 해당 아이템은 판매할 수 없도록 설정
- skript 2.5.3 버전에서 만든 돈 스크립트와 연동해야 함
- Skill 판매 금지 설정
  - `/스킬트리 판매허용 <이름>`을 입력하면 `<이름>`에 해당하는 스킬트리가 열림
  - 스킬트리의 아이템을 클릭하면 해당 아이템은 판매할 수 있도록 설정
- skript 2.5.3 버전에서 만든 돈 스크립트와 연동해야 함