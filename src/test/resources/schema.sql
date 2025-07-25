-- Test database schema for JPA tests
CREATE TYPE grade_enum AS ENUM (
  'AGE_5',
  'AGE_6',
  'AGE_7',
  'ELEMENTARY_1',
  'ELEMENTARY_2',
  'ELEMENTARY_3',
  'ELEMENTARY_4',
  'ELEMENTARY_5',
  'ELEMENTARY_6',
  'MIDDLE_1',
  'MIDDLE_2',
  'MIDDLE_3',
  'HIGH_1',
  'HIGH_2',
  'HIGH_3',
  'COLLEGE_GENERAL'
);
CREATE TYPE province_enum AS ENUM (
  'SEOUL',
  'BUSAN',
  'DAEJEON',
  'DAEGU',
  'GWANGJU',
  'INCHEON',
  'ULSAN',
  'GYEONGGI',
  'GANGWON',
  'CHUNGBUK',
  'CHUNGNAM',
  'JEONBUK',
  'JEONNAM',
  'GYEONGBUK',
  'GYEONGNAM',
  'JEJU',
  'ETC_OVERSEAS'
);