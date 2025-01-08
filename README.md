# Readme

## 0. 실행 환경
- jdk 17 (corretto-17.0.13)

## 1. 구현 범위
- 구현 1 ~ 4

## 2. 코드 빌드, 테스트, 실행 방법
### 2-1. 코드 빌드 방법
- mvn clean install
### 2-2. 테스트 방법
- mvn clean install 시 테스트 함께 수행
- 혹은 src/main/test/java/com/musinsa 디렉토리에서 테스트 실행
  - integration/IntegrationTest에서 통합 테스트 수행 가능
    - 소스에 포함된 H2 DB에 실제 데이터를 조작하여 테스트 가능
    - Run IntegrationTest로 통합 테스트 수동 실행
  - service/ProductServiceTest에서 단위 테스트 수행 가능
      - Mock 객체 사용하여 수행되는 로직만 테스트
      - Run ProductServiceTest로 단위 테스트 수동 실행
### 2-3. 실행 방법
- mvn clean install 또는 mvn clean package로 생성된 musinsa-1.0-SNAPSHOT.jar 파일을 아래 커맨드로 실행
  - java -jar musinsa-1.0-SNAPSHOT.jar 
- http://localhost:8080/swagger-ui/index.html 에서 Swagger 확인 가능
- GET http://localhost:8080/api/products/min-by-category-brand 호출하여 구현 1 확인 가능
  - Request Parameters 없음 
- GET http://localhost:8080/api/products/min-for-all-by-brand 호출하여 구현 2 확인 가능
  - Request Parameters 없음
- GET http://localhost:8080/api/products/min-max-by-category 호출하여 구현 3 확인 가능
  - Request Parameter: category(String)
  - ex) http://localhost:8080/api/products/min-max-by-category?category=top
- POST http://localhost:8080/api/products 호출하여 구현 4 확인 가능
  - Request Parameter: command(String) - [add|update|delete]
  - Request Body: 
  ```JSON
  {
    "id": 9007199254740991,
    "category": "string",
    "price": 1073741824,
    "brand_name": "string"
  }
  ```
  - ex) http://localhost:8080/api/products?command=update
  ```JSON
  {
    "id": 1,
    "category": "top",
    "price": 50100,
    "brand_name": "D"
  }
  ```
# 3. 기타 추가 정보
## 3-1. 카테고리 별 최저가격 브랜드와 상품 가격, 총액을 조회하는 API
- 카테고리 중 최저가인 상품이 여러 브랜드에 있을 경우 모두 return
- 아래 형태로 응답
  ```JSON
  {
    "최저가": [
      {
        "카테고리": "top",
        "브랜드": "C",
        "가격": 10000
      },
      {
        "카테고리": "outer",
        "브랜드": "E",
        "가격": 5000
      },
      {
        "카테고리": "pants",
        "브랜드": "D",
        "가격": 3000
      },
      {
        "카테고리": "sneakers",
        "브랜드": "A",
        "가격": 9000
      },
      {
        "카테고리": "sneakers",
        "브랜드": "G",
        "가격": 9000
      },
      {
        "카테고리": "bag",
        "브랜드": "A",
        "가격": 2000
      },
      {
        "카테고리": "hat",
        "브랜드": "D",
        "가격": 1500
      },
      {
        "카테고리": "socks",
        "브랜드": "I",
        "가격": 1700
      },
      {
        "카테고리": "accessory",
        "브랜드": "F",
        "가격": 1900
      }
    ],
    "총액": 43100
  }
  ```

## 3-2. 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회하는 API
- 최저가가 여러 브랜드일 수 있기 때문에 List 형태로 return
- D와 E의 최저가 총합이 동일할 경우
  ```JSON
  {
    "최저가": [
      {
        "브랜드": "D",
        "카테고리": [
          {
            "카테고리": "top",
            "가격": 10100
          },
          {
            "카테고리": "outer",
            "가격": 5100
          },
          {
            "카테고리": "pants",
            "가격": 3000
          },
          {
            "카테고리": "sneakers",
            "가격": 9500
          },
          {
            "카테고리": "bag",
            "가격": 2500
          },
          {
            "카테고리": "hat",
            "가격": 1500
          },
          {
            "카테고리": "socks",
            "가격": 2400
          },
          {
            "카테고리": "accessory",
            "가격": 2000
          }
        ],
        "총액": 36100
      },
      {
        "브랜드": "E",
        "카테고리": [
          {
            "카테고리": "top",
            "가격": 10100
          },
          {
            "카테고리": "outer",
            "가격": 5100
          },
          {
            "카테고리": "pants",
            "가격": 3000
          },
          {
            "카테고리": "sneakers",
            "가격": 9500
          },
          {
            "카테고리": "bag",
            "가격": 2500
          },
          {
            "카테고리": "hat",
            "가격": 1500
          },
          {
            "카테고리": "socks",
            "가격": 2400
          },
          {
            "카테고리": "accessory",
            "가격": 2000
          }
        ],
        "총액": 36100
      }
    ]
  }
  ```
  
## 3-3. 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API
- 특이사항 없음

## 3-4. 브랜드 및 상품을 추가 / 업데이트 / 삭제하는 API
- command Parameter에는 add/update/delete 사용 가능
  - 그 외 다른 command는 에러 응답

## 3-5. 데이터 및 API 사용 관련 가정 사항
- 호출 하는 측(ex. Frontend)에서는 각 Product의 id를 알고 있다고 가정한다.
  - 즉, 기본 Product 정보를 DB에서 가져올 때 id를 함께 가져온다고 가정한다. 
- 준비된 데이터는 소스에 포함된 H2 DB에 프로그램 시작 시 Initialize 된다.
- 카테고리는 상의(top), 아우터(outer), 바지(pants), 스니커즈(sneakers), 가방(bag), 모자(hat), 양말(socks), 액세서리(accessory) 등 8종만 있다고 가정
  - 그 외 카테고리는 추가되지 않는다
- 같은 브랜드, 같은 카테고리, 같은 가격의 상품이 등록될 수 있다
  - 이로 인하여 DB에는 ID 컬럼 생성하여 ID를 통해 product 관리
- Frontend에서 4번 API를 호출할 때는 ID를 기반으로 수행한다
  - add의 경우 id가 자동 채번되기 때문에 id 값이 없어야 함
    - id가 있을 경우 에러 응답
  - update의 경우 id를 통해 정보를 수정하기 때문에 id 값 필수
    - id가 없을 경우 에러 응답
  - delete의 경우 id를 통해 정보를 삭제하기 때문에 id 값 필수
    - id가 없을 경우 에러 응답