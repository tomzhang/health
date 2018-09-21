
db.p_track_question.update({repeatAsk:{$exists:true},"repeatAsk.repeats":{$exists:false}},
	{$unset:{repeatAsk:''}},
	{multi:true}
);