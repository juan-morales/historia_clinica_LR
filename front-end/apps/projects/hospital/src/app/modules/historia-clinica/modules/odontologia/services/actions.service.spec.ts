import { TestBed } from '@angular/core/testing';

import { ActionsService, ActionType } from './actions.service';

describe('ActionsService', () => {
  let service: ActionsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = new ActionsService();
  });

  it('should draw first finding', (done: DoneFn) => {

	service.setActions([]);
	service.setFinding('1',[]);

	service.currentDraw$.subscribe(newDraw => {
		expect(newDraw.whole.type === ActionType.PROCEDURE).toBeFalse();
		expect(newDraw.whole.sctid).toEqual('1');
		done();
	})
  });
});
