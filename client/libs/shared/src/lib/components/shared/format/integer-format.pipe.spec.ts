import { IntegerFormatPipe } from './integer-format.pipe';

describe('integer format pipe', () => {
  it('transform', () => {
    const pipe = new IntegerFormatPipe('en');

    expect(pipe.transform(1)).toEqual('1');
    expect(pipe.transform(12)).toEqual('12');
    expect(pipe.transform(123)).toEqual('123');
    expect(pipe.transform(1324)).toEqual('1.324');
    expect(pipe.transform(12345)).toEqual('12.345');
    expect(pipe.transform(123456)).toEqual('123.456');
    expect(pipe.transform(1234567)).toEqual('1.234.567');
    expect(pipe.transform(12345678)).toEqual('12.345.678');
    expect(pipe.transform(123456789)).toEqual('123.456.789');
    expect(pipe.transform(1234567890)).toEqual('1.234.567.890');
  });

  it('transform fr', () => {
    const pipe = new IntegerFormatPipe('fr');

    expect(pipe.transform(1)).toEqual('1');
    expect(pipe.transform(12)).toEqual('12');
    expect(pipe.transform(123)).toEqual('123');
    expect(pipe.transform(1324)).toEqual('1 324');
    expect(pipe.transform(12345)).toEqual('12 345');
    expect(pipe.transform(123456)).toEqual('123 456');
    expect(pipe.transform(1234567)).toEqual('1 234 567');
    expect(pipe.transform(12345678)).toEqual('12 345 678');
    expect(pipe.transform(123456789)).toEqual('123 456 789');
    expect(pipe.transform(1234567890)).toEqual('1 234 567 890');
  });
});
