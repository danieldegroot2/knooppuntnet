// this class is generated, please do not modify

import {List} from "immutable";
import {TagDetail} from "./tag-detail";

export class TagDiffs {

  constructor(readonly mainTags: List<TagDetail>,
              readonly extraTags: List<TagDetail>) {
  }

  public static fromJSON(jsonObject): TagDiffs {
    if (!jsonObject) {
      return undefined;
    }
    return new TagDiffs(
      jsonObject.mainTags ? List(jsonObject.mainTags.map(json => TagDetail.fromJSON(json))) : List(),
      jsonObject.extraTags ? List(jsonObject.extraTags.map(json => TagDetail.fromJSON(json))) : List()
    );
  }
}
